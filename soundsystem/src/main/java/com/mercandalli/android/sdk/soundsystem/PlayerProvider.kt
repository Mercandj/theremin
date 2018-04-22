package com.mercandalli.android.sdk.soundsystem

import android.content.Context
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerProvider {
    companion object {

        @JvmStatic
        fun create(context: Context): Player {
            val renderersFactory = DefaultRenderersFactory(
                    context,
                    null,
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            val simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, DefaultTrackSelector())
            val dataSourceFactory = DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, "MusicPlayer"))
            return SimpleExoPlayerWrapper(
                    simpleExoPlayer,
                    dataSourceFactory)
        }
    }

}
