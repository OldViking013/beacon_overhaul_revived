package com.oldviking.beacon_overhaul_revived.client.mixin;

import com.oldviking.beacon_overhaul_revived.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")
abstract class PowerButtonMixin {
    @Shadow(aliases = "this$0")
    @Final
    BeaconScreen outer;

    @Shadow
    private @NonNull Holder<MobEffect> effect;

    @Unique
    private boolean isUpgrade() {
        return this.getClass()
                .getName()
                .endsWith("$BeaconUpgradePowerButton");
    }

    @Unique
    private void setTieredTooltip(Holder<MobEffect> effect) {
        ((AbstractWidgetAccessor) this).invokeSetTooltip(Tooltip.create(BeaconPowerTooltips.createTooltip(this.outer, effect, this.isUpgrade()), null));
    }

    @Inject(method = "setEffect(Lnet/minecraft/core/Holder;)V", at = @At("RETURN"), require = 1, allow = 1)
    private void setTieredTooltip(final Holder<MobEffect> effect, final CallbackInfo ci) {
        this.setTieredTooltip(this.effect);
    }

    @Inject(method = "updateStatus(I)V", at = @At("TAIL"), require = 1, allow = 1)
    private void updateTieredTooltip(final CallbackInfo ci) {
        if (!this.isUpgrade()) {
            this.setTieredTooltip(this.effect);
        }
    }
}
