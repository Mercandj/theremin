package com.mercandalli.android.apps.theremin.gpio

import android.content.ContentValues
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.mercandalli.android.apps.theremin.main.MainGraph
import java.io.IOException
import com.mercandalli.android.apps.theremin.main_thread.MainThreadPost

class GpioManagerImpl private constructor(
        private val peripheralManagerService: PeripheralManagerService,
        private val mainThreadPost: MainThreadPost) : GpioManager {

    private var echoGpio: Gpio? = null
    private var triggerGpio: Gpio? = null

    private var time1: Long = 0
    private var time2: Long = 0
    private var keepBusy: Int = 0

    private var measureDistanceOn = false
    private var distancesMeasured = DistanceFifo(30)

    private val thread = Thread(Runnable {
        while (true) {
            if (measureDistanceOn) {
                try {
                    readDistanceSync()
                    Thread.sleep(0, 40_000)
                } catch (e: IOException) {
                    Log.e(TAG, "IOException thread", e)
                } catch (e: InterruptedException) {
                    Log.e(TAG, "InterruptedException thread", e)
                }
            } else {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Log.e(ContentValues.TAG, "InterruptedException 2 thread", e)
                }
            }
        }
    })

    init {
        // Initialize PeripheralManagerService
        val service = PeripheralManagerService()

        // List all available GPIOs
        Log.d(TAG, "Available GPIOs: " + service.gpioList)

        try {
            // Create GPIO connection.
            echoGpio = service.openGpio(GpioManager.ECHO_PIN_NAME)
            // Configure as an input.
            echoGpio!!.setDirection(Gpio.DIRECTION_IN)
            // Enable edge trigger events.
            echoGpio!!.setEdgeTriggerType(Gpio.EDGE_BOTH)
            // Set Active type to HIGH, then the HIGH events will be considered as TRUE
            echoGpio!!.setActiveType(Gpio.ACTIVE_HIGH)

        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }

        try {
            // Create GPIO connection.
            triggerGpio = service.openGpio(GpioManager.TRIGGER_PIN_NAME)

            // Configure as an output with default LOW (false) value.
            triggerGpio!!.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }

        thread.priority = Thread.MAX_PRIORITY
        thread.start()
    }

    override fun open(name: String): Gpio {
        var gpio: Gpio? = null
        try {
            gpio = peripheralManagerService.openGpio(name)
        } catch (e: IOException) {
            Log.e(TAG, "open error", e)
        }
        return gpio!!
    }

    override fun close(gpio: Gpio?) {
        if (gpio != null) {
            try {
                gpio.close()
            } catch (e: IOException) {
                Log.e(TAG, "close error", e)
            }
        }
    }

    override fun write(gpio: Gpio, on: Boolean) {
        // Initialize the pin as a high output
        try {
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH)

            // Low voltage is considered active
            gpio.setActiveType(Gpio.ACTIVE_HIGH)

            // Toggle the value to be LOW
            gpio.value = on
        } catch (e: IOException) {
            Log.e(TAG, "write error", e)
        }
    }

    override fun read(gpio: Gpio): Boolean {
        // Initialize the pin as an input
        try {
            gpio.setDirection(Gpio.DIRECTION_IN)

            // High voltage is considered active
            gpio.setActiveType(Gpio.ACTIVE_HIGH)

            // Read the active high pin state
            return gpio.value
        } catch (e: IOException) {
            Log.e(TAG, "Error read gpio $gpio", e)
        }
        return false
    }

    override fun startDistanceMeasure() {
        measureDistanceOn = true
    }

    override fun stopDistanceMeasure() {
        measureDistanceOn = false
    }

    override fun getDistance(): Int {
        return Math.min(GpioManager.DISTANCE_HARDCODED_MAX, distancesMeasured.distance)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun readDistanceSync() {
        // Just to be sure, set the trigger first to false
        triggerGpio!!.value = false
        Thread.sleep(0, 2_000)

        // Hold the trigger pin HIGH for at least 10 us
        triggerGpio!!.value = true
        Thread.sleep(0, 10_000) //10 microsec

        // Reset the trigger pin
        triggerGpio!!.value = false

        val time0 = System.nanoTime()

        // Wait for pulse on ECHO pin
        while (!echoGpio!!.value && System.nanoTime() - time0 < GpioManager.DISTANCE_MAX_TIME_TO_WAIT) {
            // keep the while loop busy
            keepBusy = 0
        }
        time1 = System.nanoTime()

        // Wait for the end of the pulse on the ECHO pin
        while (echoGpio!!.value && System.nanoTime() - time1 < GpioManager.DISTANCE_MAX_TIME_TO_WAIT) {
            // keep the while loop busy
            keepBusy = 1
        }
        time2 = System.nanoTime()

        // Measure how long the echo pin was held high (pulse width)
        val pulseWidth = time2 - time1

        // Calculate distance in centimeters. The constants
        // are coming from the datasheet, and calculated from the assumed speed
        // of sound in air at sea level (~340 m/s).
        val distance = pulseWidth / 1000.0 / 58.23 //cm

        // or we could calculate it withe the speed of the sound:
        //double distance = (pulseWidth / 1000000000.0) * 340.0 / 2.0 * 100.0;

        Log.i(TAG, "distance: $distance cm")
        mainThreadPost.post(Runnable {
            distancesMeasured.add(distance)
        })
    }

    companion object {

        private val TAG = "GpioManager jm/debug"

        @JvmStatic
        private var instance: GpioManager? = null

        fun getInstanceInternal(): GpioManager {
            if (instance == null) {
                Log.d(TAG, "Create GpioManagerImpl")
                instance = GpioManagerImpl(
                        PeripheralManagerService(),
                        MainGraph.get().provideMainThreadPost())

            }
            return instance!!
        }
    }
}
