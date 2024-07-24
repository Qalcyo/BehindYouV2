package org.polyfrost.behindyou.config

import net.minecraft.client.Minecraft
import org.polyfrost.polyui.animate.Animation
/* TODO: Animation stuff
class EaseOutQuart(duration: Float, start: Float, end: Float, reverse: Boolean): Animation(duration, start, end, reverse) {

    private var startTime = 0L

    init {
        startTime = Minecraft.getSystemTime()
    }

    override fun get(): Float {
        timePassed = (Minecraft.getSystemTime() - startTime).toFloat()
        if (timePassed >= duration) return start + change
        return animate(timePassed / duration) * change + start
    }

    override fun isFinished(): Boolean {
        return timePassed >= duration
    }

    override fun animate(x: Float) = -1 * (x - 1) * (x - 1) * (x - 1) * (x - 1) + 1
}
 */