package com.oldviking.beacon_overhaul_revived.client.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(LightTexture.class)
abstract class LightTextureMixin {
    @Shadow
    @Final
    private @NonNull Minecraft minecraft;

    @ModifyVariable( method = "updateLightTexture(F)V",
            at = @At(
                    value = "STORE"
            ),
            index = 13
    )
    private float fullBrightNightVision(float skyLight){
        final LocalPlayer player = this.minecraft.player;

        if (player == null) {
            return skyLight;
        }

        final MobEffectInstance nightVision = player.getEffect(MobEffects.NIGHT_VISION);
        return ((nightVision != null) && (nightVision.getAmplifier() > 0)) ? 15.0F : skyLight;
    }
}
