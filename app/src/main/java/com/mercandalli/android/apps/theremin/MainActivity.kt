package com.mercandalli.android.apps.theremin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.mercandalli.android.sdk.soundsystem.ThereminManager

class MainActivity : AppCompatActivity() {

    private lateinit var thereminManager: ThereminManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Graph.init(this)
        thereminManager = Graph.thereminManager

        findViewById<SeekBar>(R.id.activity_main_seekbar_distance).setOnSeekBarChangeListener(
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
    }

    private fun syncSeekbar(seekBar: SeekBar) {
        val distance = seekBar.progress
        thereminManager.onDistanceChanged(distance)
    }
}
