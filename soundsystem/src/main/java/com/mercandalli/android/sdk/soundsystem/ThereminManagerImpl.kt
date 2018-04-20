package com.mercandalli.android.sdk.soundsystem

import android.content.Context
import android.os.Handler
import com.mercandalli.android.sdk.soundsystem.lesson.ParseSteps
import com.mercandalli.android.sdk.soundsystem.lesson.Step

class ThereminManagerImpl(
        private val audioManager: AudioManager,
        private val context: Context
) : ThereminManager {

    private val samples = listOf(
            "wav/shape-of-you/dpm_shape_of_you_a_bass_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_bass_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_bass_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_bass_04.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_hat_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_kick_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_melody_04.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_snare_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_a_vox_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_bass_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_bass_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_bass_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_bass_04.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_hat_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_kick_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_melody_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_melody_02.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_melody_03.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_melody_04.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_snare_01.wav",
            "wav/shape-of-you/dpm_shape_of_you_b_vox_01.wav")

    private val handler = Handler()
    private var runnable = Runnable { runnableJob() }
    private var indexStep = 0
    private var steps: List<Step>
    private var deck = "a"

    init {
        audioManager.load(samples)
        steps = ParseSteps(context).parse()
        runnableJob()
    }

    private var volume: Float = 0f

    override fun onDistanceChanged(distance: Int) {
        volume = 1f - distance.toFloat() / 100f
        audioManager.setVolume(volume * 8)
    }

    private fun runnableJob() {
        audioManager.setVolume(volume)
        val step = steps[indexStep]
        val files = step.files
        for (file in files) {
            audioManager.play("wav/shape-of-you/dpm_shape_of_you_" + deck + "_$file.wav")
        }
        handler.removeCallbacks(runnable)
        val time = if (indexStep == 0) {
            (step.time * 1000).toLong()
        } else {
            ((step.time - steps[indexStep - 1].time) * 1000).toLong()
        }
        increaseIndexSample()
        handler.postDelayed(runnable, time)
    }

    private fun increaseIndexSample() {
        indexStep++
        if (indexStep >= steps.size) {
            indexStep = 0
            deck = if (deck == "a") "b" else "a"
        }
    }

}