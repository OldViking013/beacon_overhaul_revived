package com.oldviking.beacon_overhaul_revived.mixin;

import com.oldviking.beacon_overhaul_revived.TieredBeacon;
import com.oldviking.beacon_overhaul_revived.tags.PotencyTier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeaconMenu.class)
abstract class BeaconMenuMixin extends AbstractContainerMenu implements TieredBeacon {
    @Shadow
    @Final
    private @NonNull ContainerData beaconData;

    BeaconMenuMixin(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    @Unique
    public final PotencyTier getTier() {
        return PotencyTier.values()[this.beaconData.get(3)];
    }

    @ModifyConstant(
            method = "<init>(ILnet/minecraft/world/Container;)V",
            require = 1, allow = 1, constant = @Constant(intValue = 3))
    private static int getNewDataCount(int dataCount) {
        return 3 + 1;
    }

    @ModifyConstant(
            method =
                    "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;"
                            + "Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            constant = @Constant(intValue = 3, ordinal = 0), require = 1, allow = 1)
    private int getDataPreconditionCount(int dataCount) {
        return 3 + 1;
    }
}
