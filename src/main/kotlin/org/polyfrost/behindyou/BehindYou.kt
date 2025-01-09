package org.polyfrost.behindyou

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.behindyou.config.BehindYouConfig
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.unit.seconds
import org.polyfrost.universal.UMinecraft

@Mod(
    modid = BehindYou.MODID,
    name = BehindYou.NAME,
    version = BehindYou.VERSION,
    clientSideOnly = true,
    modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter"
)
object BehindYou {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    private var zAnimation: Animation = Animations.EaseOutQuart.create(0.5.seconds, 4f, 4f)
    private var fovAnimation: Animation = Animations.EaseOutQuart.create(0.5.seconds, 90f, 90f)
    val isFinished get() = zAnimation.isFinished

    var fov: Float
        get() = Minecraft.getMinecraft().gameSettings.fovSetting
        set(value) {
            val mc = Minecraft.getMinecraft()
            //mc.renderGlobal.setDisplayListEntitiesDirty()
            mc.gameSettings.fovSetting = value
        }

    /**
     * 0: normal
     * 1: front
     * 2: back
     */
    var perspective: Int
        get() = Minecraft.getMinecraft().gameSettings.thirdPersonView
        set(value) {
            previousPerspective = perspective
            val mc = Minecraft.getMinecraft()
            //mc.renderGlobal.setDisplayListEntitiesDirty()
            setTargetLevel(if (value == 0) 0.1f else 2f)
            mc.gameSettings.thirdPersonView = value
        }
    var previousPerspective = perspective
        private set


    fun getLevel(zIn: Double, partialTicks: Float): Double {
        if (!BehindYouConfig.enabled) return zIn
        val deltaTime = partialTicks.toNanoseconds()
        fov = fovAnimation.update(deltaTime)
        return zAnimation.update(deltaTime).toDouble()
    }

    fun modifyAnimations(duration: Long, curve: Animations) {
        zAnimation = curve.create(duration, zAnimation.value, zAnimation.to)
        fovAnimation = curve.create(duration, fovAnimation.value, fovAnimation.to)
    }

    fun setTargetLevel(z: Float) {
        zAnimation.to = z
        zAnimation.from = zAnimation.value
        zAnimation.reset()
    }

    fun toPrevious() {
        perspective = previousPerspective

    }

    // partial ticks are a fraction (0..1) of a tick, which is 50ms.
    fun Float.toNanoseconds() = (this * 50_000_000f).toLong()

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {

    }
}