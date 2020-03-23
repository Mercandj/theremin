package com.mercandalli.android.sdk.soundsystem

interface ThereminManager {

    fun onDistanceChanged(distance: Int)

    fun getPitch(): Float

    fun getSpeed(): Float

    fun registerThereminListener(listener: ThereminListener)

    fun unregisterThereminListener(listener: ThereminListener)

    interface ThereminListener {

        fun onPitchChanged()

        fun onSpeedChanged()
    }
}
