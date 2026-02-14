package com.oldviking.beacon_overhaul_revived.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconBlockEntity.class)
interface BeaconBlockEntityInvoker {
    @Invoker("filterEffect")
    static @Nullable Holder<MobEffect> filterEffect(@Nullable Holder<MobEffect> effect) {
        throw new AssertionError();
    }
}
