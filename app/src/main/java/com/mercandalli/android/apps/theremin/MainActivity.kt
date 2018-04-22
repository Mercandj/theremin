package com.mercandalli.android.apps.theremin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import com.mercandalli.android.sdk.soundsystem.ThereminManager

class MainActivity : AppCompatActivity() {

    private lateinit var thereminManager: ThereminManager
    private val thereminListener = createThereminListener()
    private lateinit var distanceTextView: TextView
    private lateinit var distanceSeekBar: SeekBar
    private lateinit var speedTextView: TextView
    private lateinit var speedSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Graph.init(this)
        thereminManager = Graph.thereminManager

        distanceTextView = findViewById(R.id.activity_main_distance_text)
        distanceSeekBar = findViewById(R.id.activity_main_distance_seekbar)
        distanceSeekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        syncSeekbar(seekBar!!)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        syncSeekbar(seekBar!!)
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        syncSeekbar(seekBar!!)
                    }
                })
        speedTextView = findViewById(R.id.activity_main_speed_text)
        speedSeekBar = findViewById(R.id.activity_main_speed_seekbar)

        thereminManager.registerThereminListener(thereminListener)
        syncSpeedAndPitchUI()
    }

    private fun syncSeekbar(seekBar: SeekBar) {
        val distanceInt = seekBar.progress
        distanceTextView.text = if (distanceInt >= 100) "Distance: >100 cm" else "Distance: $distanceInt cm"
        distanceSeekBar.progress = distanceInt
        thereminManager.onDistanceChanged(distanceInt)
    }

    private fun createThereminListener(): ThereminManager.ThereminListener {
        return object : ThereminManager.ThereminListener {
            override fun onPitchChanged() {
                syncSpeedAndPitchUI()
            }

            override fun onSpeedChanged() {
                syncSpeedAndPitchUI()
            }
        }
    }

    private fun syncSpeedAndPitchUI() {
        val speed = (thereminManager.getSpeed() * 100f).toInt()
        speedTextView.text = "Speed: $speed %"
        speedSeekBar.progress = speed
    }
}
