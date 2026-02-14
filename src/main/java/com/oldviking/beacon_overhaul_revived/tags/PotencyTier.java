package com.oldviking.beacon_overhaul_revived.tags;

import com.oldviking.beacon_overhaul_revived.BeaconOverhaulRevived;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public enum PotencyTier {
    NONE,
    LOW,
    HIGH;

    public static final TagKey<Block> LOW_POTENCY_BLOCKS = createBlockTag("low_potency");
    public static final TagKey<Block> HIGH_POTENCY_BLOCKS = createBlockTag("high_potency");

    private static TagKey<Block> createBlockTag(final String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, name));
    }
}
