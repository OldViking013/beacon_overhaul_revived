package com.oldviking.beacon_overhaul_revived.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
abstract class FogRendererMixin {
    @ModifyVariable(
            method = "computeFogColor",
            at = @At(
                    value = "STORE"
            ),
            index = 16)
    private float skipNightVisionColorShift(float scale, Camera camera) {
        final MobEffectInstance nightVision = ((LivingEntity) camera.entity()).getEffect(MobEffects.NIGHT_VISION);

        if ((nightVision != null) && (nightVision.getAmplifier() > 0)) {
            return 0.0F;
        }
        return scale;
    }
}
