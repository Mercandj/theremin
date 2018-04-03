package com.mercandalli.android.apps.theremin.gpio

import com.google.android.things.pio.Gpio

interface GpioManager {

    fun open(name: String): Gpio

    fun close(gpio: Gpio?)

    fun write(gpio: Gpio, on: Boolean)

    fun read(gpio: Gpio): Boolean

    fun startDistanceMeasure()

    fun stopDistanceMeasure()

    fun getDistance(): Int

    companion object {

        const val GPIO_7_NAME = "BCM7"

        const val TRIGGER_PIN_NAME = "BCM23"
        const val ECHO_PIN_NAME = "BCM24"

        const val DISTANCE_MAX_COMPUTED = 150 // cm
        const val DISTANCE_MAX_TIME_TO_WAIT = 58.23 * 1000.0 * DISTANCE_MAX_COMPUTED // nano
        const val DISTANCE_HARDCODED_MAX = 100 // cm
    }

}
