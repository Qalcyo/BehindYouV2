package org.polyfrost.behindyou

import club.sk1er.patcher.config.PatcherConfig
import org.polyfrost.universal.*
import org.polyfrost.universal.wrappers.UPlayer
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.*
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.polyfrost.behindyou.config.BehindYouConfig
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.utils.v1.dsl.openUI
import java.io.File
import kotlin.math.abs

val mc: Minecraft get() = UMinecraft.getMinecraft()

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

    var backDown = false
    var frontDown = false

    private var previousPerspective = 0
    private var vPerspective = 0
    private var previousFOV = 0f
    var realPerspective = 0

    private var previousBackKey = false
    private var backToggled = false
    private var previousFrontKey = false
    private var frontToggled = false

    private var to = 0f
    var distance = 0f
    private var animation: Animation = DummyAnimation(0f)
    private var lastParallaxFix = false
    private var isPatcher = false

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        isPatcher = Loader.isModLoaded("patcher")
        MinecraftForge.EVENT_BUS.register(this)
        CommandManager.registerCommand(BehindYouCommand())
    }

    @SubscribeEvent
    fun ticks(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        onTick()
        val thirdPersonView = mc.gameSettings.thirdPersonView
        if (BehindYouConfig.enabled && thirdPersonView != realPerspective) {
            setPerspective(thirdPersonView)
        }
    }

    fun level(): Float {
        val parallaxFix = isPatcher && PatcherConfig.parallaxFix
        if (realPerspective == 0) {
            if (mc.gameSettings.thirdPersonView != 0) {
                mc.gameSettings.thirdPersonView = 0
                mc.renderGlobal.setDisplayListEntitiesDirty()
            }

            if (animation !is DummyAnimation || !(animation.value == -0.05f || animation.value == 0.1f) || lastParallaxFix != parallaxFix) {
                lastParallaxFix = parallaxFix
                animation = DummyAnimation(if (parallaxFix) -0.05f else 0.1f)
            }
        } else {
            println("distance: $distance")
            if (to != 0.3f) {
                to = distance
                println("setting to: $to")
            }

            if (animation.value > distance) {
                println("animation.value > distance")
                animation = DummyAnimation(distance)
            } else if (animation.to != to) {
                val duration = if (BehindYouConfig.animation) {
                    (100 * abs(animation.value - to) / BehindYouConfig.speed * 1_000_000).toLong()
                } else {
                    0L
                }

                println("duration: $duration")

                animation = Animations.EaseOutQuart.create(
                    durationNanos = duration,
                    start = animation.value,
                    end = to,
                )
            }
        }

        if (animation.isFinished && animation.to == 0.3f) {
            mc.gameSettings.thirdPersonView = 0
            realPerspective = 0
            mc.renderGlobal.setDisplayListEntitiesDirty()
        }

        return animation.value
    }

    private fun onTick() {
        if (!BehindYouConfig.enabled) {
            resetAll()
            return
        }

        if (UScreen.currentScreen != null || mc.theWorld == null || !UPlayer.hasPlayer()) {
            if (BehindYouConfig.frontKeybindMode == 0 || BehindYouConfig.backKeybindMode == 0) {
                resetAll()
            }
            return
        }

        if (BehindYouConfig.backToFirst == 1) previousPerspective = 0

        if (backDown && frontDown) return

        if (backDown != previousBackKey) {
            previousBackKey = backDown

            if (backDown) {
                if (backToggled) {
                    resetBack()
                } else {
                    if (frontToggled) {
                        resetFront()
                    }
                    if (vPerspective != 2) enterBack() else resetBack()
                }
            } else if (BehindYouConfig.backKeybindMode == 0) {
                resetBack()
            }

        } else if (frontDown != previousFrontKey) {
            previousFrontKey = frontDown

            if (frontDown) {
                if (frontToggled) {
                    resetFront()
                } else {
                    if (backToggled) {
                        resetBack()
                    }
                    if (vPerspective != 1) enterFront() else resetFront()
                }
            } else if (BehindYouConfig.frontKeybindMode == 0) {
                resetFront()
            }

        }
    }

    private fun enterBack() {
        backToggled = true
        previousFOV = getFOV()
        setPerspective(2)
        if (BehindYouConfig.changeFOV) {
            setFOV(BehindYouConfig.backFOV)
        }
    }

    private fun enterFront() {
        frontToggled = true
        previousFOV = getFOV()
        setPerspective(1)
        if (BehindYouConfig.changeFOV) {
            setFOV(BehindYouConfig.frontFOV)
        }
    }

    private fun resetBack() {
        backToggled = false
        setPerspective(
            previousPerspective
        )
        setFOV(previousFOV)
    }

    private fun resetFront() {
        frontToggled = false
        setPerspective(
            previousPerspective
        )
        setFOV(previousFOV)
    }

    private fun resetAll() {
        if (frontToggled) {
            resetFront()
        }
        if (backToggled) {
            resetBack()
        }
    }

    fun setPerspective(value: Int) {
        if (vPerspective == value) return
        previousPerspective = vPerspective
        vPerspective = value

        if (value == 0) {
            to = 0.3f
            if (!BehindYouConfig.animation) {
                mc.gameSettings.thirdPersonView = 0
                realPerspective = 0
                mc.renderGlobal.setDisplayListEntitiesDirty()
            }
        } else {
            to = distance
            mc.gameSettings.thirdPersonView = value
            realPerspective = value
            mc.renderGlobal.setDisplayListEntitiesDirty()
            animation = DummyAnimation(0.3f)
        }
    }

    private fun getFOV() = mc.gameSettings.fovSetting

    private fun setFOV(value: Number) {
        mc.gameSettings.fovSetting = value.toFloat()
    }

    @Command(value = ["behindyou", "behindyouv3"], description = "Open the BehindYou config GUI.")
    class BehindYouCommand {
        @Command
        fun main() {
            BehindYouConfig.openUI()
        }
    }
}