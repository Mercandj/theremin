package com.mercandalli.android.sdk.soundsystem

import android.os.Handler

class ThereminManagerImpl(
        private val audioManager: AudioManager
) : ThereminManager {

    private val samples = listOf(
            "wav/shape-of-you/dpm_shape_of_you_a_melody_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_04.wav")

    private val handler = Handler()
    private var runnable = Runnable { runnableJob() }
    private var indexSample = 0

    init {
        audioManager.load(samples)
        runnableJob()
    }

    private var volume: Float = 0f

    override fun onDistanceChanged(distance: Int) {
        volume = 1f - distance.toFloat() / 100f
        audioManager.setVolume(volume * 6)
    }

    private fun runnableJob() {
        audioManager.setVolume(volume)
        audioManager.play(samples[indexSample])
        increaseIndexSample()
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 1300)
    }

    private fun increaseIndexSample() {
        indexSample++
        if (indexSample >= samples.size) {
            indexSample = 0
        }
    }

}