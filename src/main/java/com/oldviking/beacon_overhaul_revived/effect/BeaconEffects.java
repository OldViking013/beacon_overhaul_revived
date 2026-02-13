package com.oldviking.beacon_overhaul_revived.effect;

import com.oldviking.beacon_overhaul_revived.BeaconOverhaulRevived;
import com.oldviking.beacon_overhaul_revived.mixin.BeaconBlockEntityAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeaconEffects {
    public static final Holder<MobEffect> LONG_REACH =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "long-reach"),
                    new LongReachEffect().addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "long-block-reach"), 2.0F, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "long-entity-reach"), 2.0F, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> NUTRITION =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "nutrition"),
                    new NutritionEffect());

    static {
        addEffectsToBeacon();
    }

    public static void registerBeaconEffects() {
        BeaconOverhaulRevived.LOGGER.info("Registering Beacon Effects");
    }

    public static void addEffectsToBeacon() {
        final List<List<Holder<MobEffect>>> effects = BeaconBlockEntity.BEACON_EFFECTS.stream().map(ArrayList::new).collect(Collectors.toCollection(ArrayList::new));

        effects.get(0).add(MobEffects.NIGHT_VISION);
        effects.get(1).add(LONG_REACH);
        effects.get(2).add(NUTRITION);
        effects.get(3).add(MobEffects.FIRE_RESISTANCE);
        effects.get(3).add(MobEffects.SLOW_FALLING);

        BeaconBlockEntityAccessor.setBeaconEffects(effects);
        BeaconBlockEntityAccessor.setValidEffects(effects.stream().flatMap(List::stream).collect(Collectors.toSet()));
    }
}
