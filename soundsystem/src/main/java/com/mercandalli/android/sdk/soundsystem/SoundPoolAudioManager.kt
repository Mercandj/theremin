package com.mercandalli.android.sdk.soundsystem

import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

internal class SoundPoolAudioManager constructor(
    private val assetManager: AssetManager
) : AudioManager {

    private val soundPool: SoundPool = SoundPool(
        20,
        android.media.AudioManager.STREAM_MUSIC,
        0
    )

    private var loaded = HashMap<Int, Boolean>()
    private var slots = HashMap<String, Int>()
    private var volumes = HashMap<String, Float>()

    private var onPausedListener: AudioManager.OnPausedListener? = null

    override fun load(assetsFilePaths: List<String>) {
        for (assetsFilePath in assetsFilePaths) {
            slots[assetsFilePath] = loadSound(assetsFilePath)
        }
    }

    override fun play(assetsFilePath: String) {
        if (loaded.containsKey(slots[assetsFilePath]) && loaded[slots[assetsFilePath]]!!) {
            val volume = if (volumes.containsKey(assetsFilePath)) volumes[assetsFilePath]!! else 1f
            soundPool.play(slots[assetsFilePath]!!, volume, volume, 1, 0, 1f)
        }
    }

    override fun setSineFrequency(frequency: Double) {
    }

    override fun setOnPausedListener(listener: AudioManager.OnPausedListener?) {
        onPausedListener = listener
    }

    override fun setVolume(volume: Float) {
        for (key in slots.keys) {
            volumes[key] = volume
            soundPool.setVolume(slots[key]!!, volume, volume)
        }
    }

    @Suppress("ObjectLiteralToLambda")
    private fun loadSound(strSound: String): Int {
        soundPool.setOnLoadCompleteListener(object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
                loaded[sampleId] = true
            }
        })
        try {
            return soundPool.load(assetManager.openFd(strSound), 1)
        } catch (e: IOException) {
            Log.e("SoundPoolAudioManager", "load error", e)
        }
        return -1
    }
}
