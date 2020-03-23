package com.mercandalli.android.sdk.soundsystem

interface AudioManager {

    fun load(assetsFilePaths: List<String>)

    fun play(assetsFilePath: String)

    fun setVolume(volume: Float)

    fun setSineFrequency(frequency: Double)

    fun setOnPausedListener(listener: OnPausedListener?)

    interface OnPausedListener {

        fun onPaused()
    }
}
