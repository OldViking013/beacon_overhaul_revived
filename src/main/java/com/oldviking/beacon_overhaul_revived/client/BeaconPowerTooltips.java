package com.oldviking.beacon_overhaul_revived.client;

import com.oldviking.beacon_overhaul_revived.TieredBeacon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

@Environment(EnvType.CLIENT)
public class BeaconPowerTooltips {
    private static final String[] EFFECT_SUFFIXES = {" II", " III", " IV"};

    private BeaconPowerTooltips(){}

    public static final MutableComponent createTooltip(
            BeaconScreen screen, Holder<MobEffect> effect, boolean upgrade) {
        final var component = Component.translatable(effect.value().getDescriptionId());

        if ((effect != MobEffects.SLOW_FALLING) && (effect != MobEffects.FIRE_RESISTANCE)) {
            var potency = upgrade ? 1 : 0;

            if (effect != MobEffects.NIGHT_VISION) {
                potency += ((TieredBeacon)screen.getMenu()).getTier().ordinal();
            }

            if (potency > 0) {
                return component.append(EFFECT_SUFFIXES[potency - 1]);
            }
        }
        return component;
    }
}
