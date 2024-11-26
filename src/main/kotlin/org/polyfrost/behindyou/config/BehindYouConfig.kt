package org.polyfrost.behindyou.config

import org.polyfrost.behindyou.BehindYou
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.universal.UKeyboard

object BehindYouConfig : Config("behindyouv3.json", "/behindyou_dark.svg", "BehindYouV3", Category.QOL) {

    @Switch(
        title = "Enabled"
    )
    var enabled = true

    @Keybind(
        title = "Frontview Keybind"
    )
    var frontKeybind = KeybindHelper.builder().keys(UKeyboard.KEY_Y).does { BehindYou.frontDown = it }.build()

    @RadioButton(
        title = "Frontview Keybind Handle Mode",
        options = ["Hold", "Toggle"]
    )
    var frontKeybindMode = 0

    @Keybind(
        title = "Backview Keybind"
    )
    var backKeybind = KeybindHelper.builder().keys(UKeyboard.KEY_U).does { BehindYou.backDown = it }.build()

    @RadioButton(
        title = "Backview Keybind Handle Mode",
        options = ["Hold", "Toggle"]
    )
    var backKeybindMode = 0

    @RadioButton(
        title = "Back To",
        options =  ["Previous", "First"]
    )
    var backToFirst = 1

    @Checkbox(
        title = "Camera Animations"
    )
    var animation = false

    @Slider(
        title = "Animation Speed",
        min = 0.1f, max = 2f
    )
    var speed = 1f

    @Switch(
        title = "Modify FOV",
    )
    var changeFOV = false

    @Slider(
        title = "Backview FOV",
        min = 30F,
        max = 110F
    )
    var backFOV = 100

    @Slider(
        title = "Frontview FOV",
        min = 30F,
        max = 110F
    )
    var frontFOV = 100

    init {
        addDependency("speed", "animation")
        addDependency("backFOV", "changeFOV")
        addDependency("frontFOV", "changeFOV")

        KeybindManager.registerKeybind(frontKeybind)
        KeybindManager.registerKeybind(backKeybind)
    }
}
