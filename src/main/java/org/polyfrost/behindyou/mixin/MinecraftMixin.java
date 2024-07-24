package org.polyfrost.behindyou.mixin;

import org.polyfrost.behindyou.BehindYou;
import org.polyfrost.behindyou.config.BehindYouConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static org.polyfrost.behindyou.BehindYouKt.enabled;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0, opcode = Opcodes.PUTFIELD))
    private void override(GameSettings instance, int value) {
        if (enabled) {
            int perspective = value;
            if (perspective > 2) perspective = 0;
            BehindYou.INSTANCE.setPerspective(perspective);
        } else {
            instance.thirdPersonView = value;
        }
    }

    @Redirect(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void override2(GameSettings instance, int value) {
        if (enabled) {
            int perspective = value;
            if (perspective > 2) perspective = 0;
            BehindYou.INSTANCE.setPerspective(perspective);
        } else {
            instance.thirdPersonView = value;
        }
    }
}
