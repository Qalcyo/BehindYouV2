package org.polyfrost.behindyou.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.polyfrost.behindyou.BehindYou;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class Mixin_MinecraftCapturePOVSet {
    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void capturePOVSetF5(GameSettings instance, int value) {
        BehindYou.INSTANCE.setPerspective(value);
    }

    @Redirect(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void capturePOVSetStuckInBlock(GameSettings instance, int value) {
        BehindYou.INSTANCE.setPerspective(value);
    }
}
