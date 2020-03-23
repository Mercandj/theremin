package com.mercandalli.android.apps.theremin

import android.content.Context
import com.mercandalli.android.sdk.soundsystem.SoundSystemModule
import com.mercandalli.android.sdk.soundsystem.ThereminManager

class Graph {

    companion object {

        @JvmStatic
        lateinit var thereminManager: ThereminManager

        @JvmStatic
        fun init(context: Context) {
            thereminManager = SoundSystemModule(context.applicationContext).provideThereminManager()
        }
    }
}
