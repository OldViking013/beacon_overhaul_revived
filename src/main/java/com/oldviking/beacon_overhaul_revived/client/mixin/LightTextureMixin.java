package com.oldviking.beacon_overhaul_revived.client.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LightmapRenderStateExtractor.class)
abstract class LightTextureMixin {
    @Shadow
    @Final
    private @NonNull Minecraft minecraft;

    @Inject(
            method = "extract",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/LightmapRenderState;nightVisionEffectIntensity:F",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void fullBrightNightVision(LightmapRenderState renderState, float partialTicks, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;

        if (player == null) {
            return;
        }

        MobEffectInstance nightVision = player.getEffect(MobEffects.NIGHT_VISION);
        if (nightVision != null && nightVision.getAmplifier() > 0) {
            renderState.nightVisionEffectIntensity = 15.0F;
        }
    }
}
