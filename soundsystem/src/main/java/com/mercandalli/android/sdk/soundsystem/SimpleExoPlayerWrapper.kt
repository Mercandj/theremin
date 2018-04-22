package com.mercandalli.android.sdk.soundsystem


import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource

internal class SimpleExoPlayerWrapper(
        private val simpleExoPlayer: SimpleExoPlayer,
        private val dataSourceFactory: DataSource.Factory) : Player {

    private var isPlayingInternal = false
    private var playerListener: Player.PlayerListener? = null

    override val isPlaying: Boolean
        get() = isPlayingInternal

    override val audioSessionId: Int
        get() = simpleExoPlayer.audioSessionId

    override val duration: Long
        get() = simpleExoPlayer.duration

    override val currentPosition: Int
        get() = simpleExoPlayer.currentPosition.toInt()

    init {
        simpleExoPlayer.addListener(createExoPlayerEventListener())
    }

    override fun load(path: String) {
        val mediaSource = ExtractorMediaSource(
                Uri.parse(path),
                dataSourceFactory,
                DefaultExtractorsFactory(),
                null,
                null)
        simpleExoPlayer.prepare(mediaSource)
        playerListener?.onPrepare(this@SimpleExoPlayerWrapper)
    }

    override fun play() {
        simpleExoPlayer.playWhenReady = true
    }

    override fun pause() {
        simpleExoPlayer.playWhenReady = false
    }

    override fun stop() {
        simpleExoPlayer.stop()
    }

    override fun release() {
        simpleExoPlayer.release()
    }

    override fun getPitch(): Float {
        return simpleExoPlayer.playbackParameters.pitch
    }

    override fun getSpeed(): Float {
        return simpleExoPlayer.playbackParameters.speed
    }

    override fun setVolume(volume: Float) {
        simpleExoPlayer.volume = volume
    }

    override fun setSpeed(speed: Float) {
        simpleExoPlayer.playbackParameters = PlaybackParameters(speed, simpleExoPlayer.playbackParameters.pitch)
    }

    override fun setPitch(pitch: Float) {
        simpleExoPlayer.playbackParameters = PlaybackParameters(simpleExoPlayer.playbackParameters.speed, pitch)
    }

    override fun seekTo(ms: Long) {
        val seekPosition = Math.min(Math.max(0, ms), duration)
        simpleExoPlayer.seekTo(seekPosition)
    }

    override fun registerListener(listener: Player.PlayerListener) {
        playerListener = listener
    }

    override fun unregisterListener(listener: Player.PlayerListener) {
        playerListener = null
    }

    private fun createExoPlayerEventListener(): ExoPlayer.EventListener {
        return object : ExoPlayer.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                playerListener?.onError(this@SimpleExoPlayerWrapper)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    ExoPlayer.STATE_BUFFERING ->
                        playerListener?.onBufferingStart(this@SimpleExoPlayerWrapper)

                    ExoPlayer.STATE_READY -> {
                        playerListener?.onBufferingComplete(this@SimpleExoPlayerWrapper)
                        isPlayingInternal = playWhenReady
                    }

                    ExoPlayer.STATE_ENDED ->
                        playerListener?.onComplete(this@SimpleExoPlayerWrapper)
                }

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPositionDiscontinuity() {

            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

            }

        }
    }
}
