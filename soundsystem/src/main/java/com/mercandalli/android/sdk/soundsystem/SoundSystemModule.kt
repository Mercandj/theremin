package com.mercandalli.android.sdk.soundsystem

import android.content.Context

class SoundSystemModule constructor(private val context: Context) {

    @Suppress("ConstantConditionIf")
    fun provideAudioManager(): AudioManager {
        if (audioManager == null) {
            audioManager = if (NativeAudioManager) {
                NativeAudioManager(context)
            } else {
                SoundPoolAudioManager(context.assets)
            }
        }
        return audioManager!!
    }

    fun provideThereminManager(): ThereminManager {
        if (thereminManager == null) {
            thereminManager = ThereminManagerImpl(provideAudioManager(), context)
        }
        return thereminManager!!
    }

    companion object {
        private const val NativeAudioManager = true

        @JvmStatic
        var audioManager: AudioManager? = null
        @JvmStatic
        var thereminManager: ThereminManager? = null
    }

}