package com.oldviking.beacon_overhaul_revived.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("VALID_EFFECTS")
    @Mutable
    static void setValidEffects(final Set<Holder<MobEffect>> value) {
        throw new AssertionError();
    }

    @Accessor("BEACON_EFFECTS")
    @Mutable
    static void setBeaconEffects(List<List<Holder<MobEffect>>> value) {
        throw new AssertionError();
    }
}
