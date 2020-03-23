package com.mercandalli.android.sdk.soundsystem

class ThereminManagerImpl(
    private val player: Player
) : ThereminManager {

    private val listeners = ArrayList<ThereminManager.ThereminListener>()

    init {
        player.load("asset:///ed_sheeran_shape_of_you_instrumental.mp3")
        player.play()
        player.registerListener(object : Player.PlayerListener {
            override fun onPrepare(player: Player) {
            }

            override fun onError(player: Player) {
            }

            override fun onBufferingStart(player: Player) {
            }

            override fun onBufferingComplete(player: Player) {
            }

            override fun onComplete(player: Player) {
                player.seekTo(0)
                player.play()
            }
        })
        setPitch(1f)
        setSpeed(1f)
    }

    override fun onDistanceChanged(distance: Int) {
        if (distance > 90) {
            if (player.isPlaying) player.pause()
            return
        }
        if (!player.isPlaying) player.play()
        if (distance > 50) {
            setPitch(1.0f)
            setSpeed(0.5f)
            return
        }
        if (distance < 10) {
            setPitch(0.5f)
            setSpeed(1.5f)
            return
        }
        val distancePercent = ((distance - 10) * (5f / 2f)) / 100f
        val distancePercentAroundZero = distancePercent - 0.5f
        setPitch(1f + distancePercentAroundZero)
        setSpeed(1f - distancePercentAroundZero)
    }

    override fun getPitch(): Float {
        return player.getPitch()
    }

    override fun getSpeed(): Float {
        return player.getSpeed()
    }

    override fun registerThereminListener(listener: ThereminManager.ThereminListener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterThereminListener(listener: ThereminManager.ThereminListener) {
        listeners.remove(listener)
    }

    private fun setPitch(pitch: Float) {
        player.setPitch(pitch)
        for (listener in listeners) {
            listener.onPitchChanged()
        }
    }

    private fun setSpeed(speed: Float) {
        player.setSpeed(speed)
        for (listener in listeners) {
            listener.onSpeedChanged()
        }
    }
}
