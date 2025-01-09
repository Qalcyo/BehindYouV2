package org.polyfrost.behindyou.mixin;

import net.minecraft.client.settings.GameSettings;
import org.polyfrost.behindyou.BehindYou;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderer.class)
public class Mixin_EntityRendererSetFOVLevel {
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0))
    private int checkAndKeepF3(GameSettings instance) {
        return BehindYou.INSTANCE.isFinished() ? instance.thirdPersonView : 1;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE"), ordinal = 3)
    private double set(double z, float partialTicks) {
        return BehindYou.INSTANCE.getLevel(z, partialTicks);
    }
}
