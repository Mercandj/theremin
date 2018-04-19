package com.mercandalli.android.apps.theremin.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.mercandalli.android.apps.theremin.R
import com.mercandalli.android.apps.theremin.application.AppUtils.launchApp
import com.mercandalli.android.apps.theremin.gpio.GpioManagerImpl
import com.mercandalli.android.apps.theremin.wifi.WifiUtils.Companion.wifiIpAddress
import com.mercandalli.android.sdk.soundsystem.ThereminManager
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint


class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private var runnableUpdateGpio7 = Runnable { runnableJob() }
    private var runnableUpdateDistance = Runnable { runnableDistance() }
    private var runnableDismissSnackbar = Runnable {
        snackbar.dismiss()
    }
    private lateinit var distanceTextView: TextView
    private lateinit var distanceSeekBar: SeekBar

    private val gpioManager = GpioManagerImpl.getInstanceInternal()
    private var gpio7RefreshRate = 300

    private lateinit var snackbar: Snackbar

    private lateinit var thereminManager: ThereminManager

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
        distanceTextView = findViewById(R.id.activity_main_distance_text)
        distanceSeekBar = findViewById(R.id.activity_main_distance_seekbar)
        snackbar = Snackbar.make(window.decorView.findViewById(android.R.id.content),
                "Something detected, so refreshing...", Snackbar.LENGTH_INDEFINITE)

        /*
        val graph = findViewById<GraphView>(R.id.activity_main_graph)
        val series = LineGraphSeries<DataPoint>(arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)))
        graph.addSeries(series)
*/
        handler.post(runnableUpdateGpio7)
        handler.post(runnableUpdateDistance)
        thereminManager = MainGraph.get().provideThereminManager()

        if (savedInstanceState == null) {
            gpioManager.startDistanceMeasure()
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
        distanceTextView.text = if (distanceInt > 100) "Distance: >100 cm" else "Distance: $distanceInt cm"
        distanceSeekBar.progress = distanceInt
        thereminManager.onDistanceChanged(distanceInt)

        if (distanceInt < 40) {
            handler.removeCallbacks(runnableDismissSnackbar)
            snackbar.show()
        } else {
            handler.postDelayed(runnableDismissSnackbar, 1_500)
        }
    }
}
