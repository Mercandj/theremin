package com.mercandalli.android.apps.theremin.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import com.mercandalli.android.apps.theremin.R
import com.mercandalli.android.apps.theremin.application.AppUtils.launchApp
import com.mercandalli.android.apps.theremin.audio.AudioManager
import com.mercandalli.android.apps.theremin.gpio.GpioManagerImpl
import com.mercandalli.android.apps.theremin.wifi.WifiUtils.Companion.wifiIpAddress
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private var runnableUpdateGpio7 = Runnable { runnableJob() }
    private var runnableUpdateDistance = Runnable { runnableDistance() }
    private var runnableDismissSnackbar = Runnable {
        snackbar.dismiss()
    }
    private lateinit var distanceTextView: TextView

    private val gpioManager = GpioManagerImpl.getInstanceInternal()
    private var gpio7RefreshRate = 300

    private lateinit var snackbar: Snackbar

    private lateinit var audioManager: AudioManager

    private val samples = listOf(
            "wav/shape-of-you/dpm_shape_of_you_a_melody_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_04.wav")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.activity_main_at_launcher)!!.setOnClickListener {
            launchApp(
                    this,
                    "com.android.iotlauncher",
                    "com.android.iotlauncher.DefaultIoTLauncher")
        }

        findViewById<TextView>(R.id.activity_main_ip)!!.text = wifiIpAddress(this).toString()
        distanceTextView = findViewById(R.id.activity_main_distance_output)
        snackbar = Snackbar.make(window.decorView.findViewById(android.R.id.content),
                "Something detected, so refreshing...", Snackbar.LENGTH_INDEFINITE)

        handler.post(runnableUpdateGpio7)
        handler.post(runnableUpdateDistance)
        audioManager = MainGraph.get().provideAudioManager()

        if (savedInstanceState == null) {
            gpioManager.startDistanceMeasure()
            audioManager.load(samples)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnableUpdateGpio7)
        handler.removeCallbacks(runnableUpdateDistance)
        handler.removeCallbacks(runnableDismissSnackbar)
        super.onDestroy()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_F1 -> {

                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun runnableJob() {
        syncDistance()
        handler.removeCallbacks(runnableUpdateGpio7)
        handler.postDelayed(runnableUpdateGpio7, gpio7RefreshRate.toLong())
    }

    private fun runnableDistance() {
        syncDistance()
        handler.removeCallbacks(runnableUpdateDistance)
        handler.postDelayed(runnableUpdateDistance, 80)
    }

    private fun syncDistance() {
        val distanceInt = gpioManager.getDistance()
        distanceTextView.text = "Distance: $distanceInt cm"

        val frequencyMax = 650.0
        val frequencyMin = 150.0

        val frequency = frequencyMin +
                (frequencyMax - frequencyMin) * distanceInt.toFloat() / 100f
        audioManager.setSineFrequency(frequency)

        if (lastPlayTime < System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3)) {
            when (distanceInt) {
                in 0..25 -> {
                    audioManager.play(samples[0])
                }
                in 25..50 -> {
                    audioManager.play(samples[1])
                }
                in 50..75 -> {
                    audioManager.play(samples[2])
                }
                in 75..100 -> {
                    audioManager.play(samples[3])
                }
            }
            lastPlayTime = System.currentTimeMillis()
        }

        if (distanceInt < 40) {
            handler.removeCallbacks(runnableDismissSnackbar)
            snackbar.show()
        } else {
            handler.postDelayed(runnableDismissSnackbar, 1_500)
        }
    }

    private var lastPlayTime: Long = 0
}
