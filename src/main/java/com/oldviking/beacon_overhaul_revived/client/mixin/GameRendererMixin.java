package com.oldviking.beacon_overhaul_revived.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
abstract class GameRendererMixin implements ResourceManagerReloadListener {
    @Inject(
            method = "getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F",
            at = @At(shift = At.Shift.BY, by = -2, value = "CONSTANT", args = "intValue=200"),
            cancellable = true, require = 1, allow = 1)
    private static void noNightVisionFlickerWhenAmbient(
            LivingEntity entity, float partialTick, CallbackInfoReturnable<Float> cir, @Local MobEffectInstance effect) {
        if (effect.isAmbient()) {
            cir.setReturnValue(1.0F);
        }
    }
}
