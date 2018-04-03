package com.mercandalli.android.apps.theremin.audio

interface AudioManager {
    fun load(assetsFilePaths: List<String>)

    fun play(assetsFilePath: String)
}
