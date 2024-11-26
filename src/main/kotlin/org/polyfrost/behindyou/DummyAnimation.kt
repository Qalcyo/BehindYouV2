package org.polyfrost.behindyou

import org.polyfrost.polyui.animate.Animation

class DummyAnimation(val theValue: Float) : Animation(0L, theValue, theValue) {
    override fun getValue(percent: Float) = value

    override fun clone() = DummyAnimation(theValue)
}
