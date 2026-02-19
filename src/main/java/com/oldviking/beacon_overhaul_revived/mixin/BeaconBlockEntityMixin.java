package com.oldviking.beacon_overhaul_revived.mixin;

import com.oldviking.beacon_overhaul_revived.MutableTieredBeacon;
import com.oldviking.beacon_overhaul_revived.TieredBeacon;
import com.oldviking.beacon_overhaul_revived.tags.PotencyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider, MutableTieredBeacon {
    @Shadow
    int levels;

    @Unique
    private PotencyTier tier =  PotencyTier.NONE;

    BeaconBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Unique
    @Override
    public final PotencyTier getTier() {
        return this.tier;
    }

    @Unique
    @Override
    public final void setTier(final PotencyTier tier) {
        this.tier = Objects.requireNonNull(tier);
    }

    @Inject(
            method =
                    "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;"
                            + "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V",
            at = @At(
                    target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;"
                            + "updateBase(Lnet/minecraft/world/level/Level;III)I",
                    shift = At.Shift.BY, by = 2, value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILHARD, require = 1, allow = 1)
    private static void updateTier(
            Level level, BlockPos pos, BlockState state, BeaconBlockEntity beaconBlock,
            CallbackInfo ci, int x, int y, int z) {
        var tier = PotencyTier.HIGH;
        var layerOffset = 1;

        layerCheck:
        while (layerOffset <= 4) {
            final var yOffset = y - layerOffset;

            if (yOffset < level.getMinY()) {
                tier = PotencyTier.NONE;
                break;
            }

            for (var xOffset = x - layerOffset; xOffset <= (x + layerOffset); ++xOffset) {
                for (var zOffset = z - layerOffset; zOffset <= (z + layerOffset); ++zOffset) {
                    final var stateAt = level.getBlockState(new BlockPos(xOffset, yOffset, zOffset));

                    if(!stateAt.is(BlockTags.BEACON_BASE_BLOCKS)) {
                        if(layerOffset == 1) {
                            tier = PotencyTier.NONE;
                        }
                        break layerCheck;
                    }
                    final PotencyTier tierAt;

                    if (stateAt.is(PotencyTier.HIGH_POTENCY_BLOCKS)) {
                        tierAt = PotencyTier.HIGH;
                    } else if (stateAt.is(PotencyTier.LOW_POTENCY_BLOCKS)) {
                        tierAt = PotencyTier.LOW;
                    } else {
                        tierAt = PotencyTier.NONE;
                    }
                    if (tierAt.ordinal() < tier.ordinal()) {
                        tier = tierAt;
                    }
                }
            }
            ++layerOffset;
        }
        ((MutableTieredBeacon)beaconBlock).setTier(tier);
    }

    @ModifyVariable(
            method =
                    "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I" +
                            "Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "STORE", ordinal = 0),
            index = 5, require = 1, allow = 1)
    private static double modifyEffectRadius(double radius, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TieredBeacon beaconBlock) {
            return radius + (10.0 * beaconBlock.getTier().ordinal());
        }
        return radius;
    }

    @ModifyVariable(
            method =
                    "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I" +
                            "Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "STORE", ordinal = 0),
            index = 7, require = 1, allow = 1)
    private static int modifyPrimaryAmplifier(
            int primaryAmplifier, Level level, BlockPos pos, int levels,
            @Nullable Holder<MobEffect> primaryEffect) {
        if(primaryEffect != MobEffects.NIGHT_VISION) {
            if(level.getBlockEntity(pos) instanceof TieredBeacon beaconBlock) {
                return beaconBlock.getTier().ordinal();
            }
        }
        return primaryAmplifier;
    }

    @ModifyVariable(
            method =
                    "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I" +
                            "Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "STORE", ordinal = 1),
            index = 7, require = 1, allow = 1)
    private static int modifyPotentPrimaryAmplifier(
            int primaryAmplifier, Level level, BlockPos pos, int levels,
            @Nullable Holder<MobEffect> primaryEffect, @Nullable Holder<MobEffect> secondaryEffect) {
        if ((primaryEffect != MobEffects.NIGHT_VISION)
        && (secondaryEffect != MobEffects.SLOW_FALLING)
        && (primaryEffect != MobEffects.FIRE_RESISTANCE)) {
            if(level.getBlockEntity(pos) instanceof TieredBeacon beaconBlock) {
                return primaryAmplifier + beaconBlock.getTier().ordinal();
            }
        }
        return primaryAmplifier;
    }

    @ModifyVariable(
            method =
                    "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I" +
                            "Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "STORE", ordinal = 0),
            index = 8, require = 1, allow = 1)
    private static int modifyDuration(int duration, Level level, BlockPos pos, int levels) {
        if(level.getBlockEntity(pos) instanceof TieredBeacon beaconBlock) {
            return ((9 * (beaconBlock.getTier().ordinal() + 1)) + (levels * 2)) * 20;
        }
        return duration;
    }

    @ModifyConstant(
            method =
                    "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I" +
                            "Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V",
            constant = @Constant(ordinal = 1),
            require = 1, allow = 1)
    private static int modifySecondaryAmplifier(
            int secondaryAmplifier, Level level, BlockPos pos, int levels,
            @Nullable Holder<MobEffect> primaryEffect, @Nullable Holder<MobEffect> secondaryEffect) {
        if ((secondaryEffect != MobEffects.SLOW_FALLING)
        && (secondaryEffect != MobEffects.FIRE_RESISTANCE)) {
            if(level.getBlockEntity(pos) instanceof TieredBeacon beaconBlock) {
                return beaconBlock.getTier().ordinal();
            }
        }
        return secondaryAmplifier;
    }

    @Mixin(targets = "net.minecraft.world.level.block.entity.BeaconBlockEntity$1")
    abstract static class BeaconBlockEntity_DataAccessMixin implements ContainerData {

        @Unique
        private BeaconBlockEntity beacon;

        @Inject(method = "<init>", at = @At("TAIL"))
        private void captureOuter(BeaconBlockEntity outer, CallbackInfo ci) {
            this.beacon = outer;
        }

        @Inject(method = "get", at = @At("HEAD"), cancellable = true)
        private void tryGetTier(int index, CallbackInfoReturnable<Integer> cir) {
            if (index == 3) {
                cir.setReturnValue(
                        ((TieredBeacon)this.beacon).getTier().ordinal()
                );
            }
        }

        @Inject(method = "set", at = @At("HEAD"), cancellable = true)
        private void trySetTier(int index, int value, CallbackInfo ci) {
            if (index == 3) {
                ((MutableTieredBeacon)this.beacon)
                        .setTier(PotencyTier.values()[value]);
                ci.cancel();
            }
        }

        @ModifyConstant(
                method = "getCount",
                constant = @Constant(intValue = 3)
        )
        private int expandDataCount(int original) {
            return 4;
        }
    }
}
