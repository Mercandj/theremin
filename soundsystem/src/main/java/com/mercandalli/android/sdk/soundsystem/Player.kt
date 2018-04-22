package com.mercandalli.android.sdk.soundsystem

interface Player {

    val audioSessionId: Int

    val isPlaying: Boolean

    val duration: Long

    val currentPosition: Int

    fun load(path: String)

    fun play()

    fun pause()

    fun stop()

    fun release()

    fun getPitch():Float

    fun getSpeed():Float

    /**
     * Sets the audio volume, with 0 being silence and 1 being unity gain.
     *
     * @param volume The audio volume.
     */
    fun setVolume(volume: Float)

    /**
     * The factor by which playback will be sped up.
     */
    fun setSpeed(speed: Float)

    /**
     * The factor by which the audio pitch will be scaled.
     */
    fun setPitch(pitch: Float)

    fun seekTo(ms: Long)

    fun registerListener(listener: PlayerListener)

    fun unregisterListener(listener: PlayerListener)

    interface PlayerListener {

        fun onComplete(player: Player)

        fun onPrepare(player: Player)

        fun onError(player: Player)

        fun onBufferingStart(player: Player)

        fun onBufferingComplete(player: Player)
    }

}
