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
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
                    shift = At.Shift.BY, by = 2, value = "INVOKE", opcode = Opcodes.INVOKESTATIC),
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
            at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0),
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
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
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
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 1),
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
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
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


//TODO Make this better Fck AI
    @Shadow
    @Nullable
    private Holder<MobEffect> primaryPower;

    @Shadow
    @Nullable
    private Holder<MobEffect> secondaryPower;

    @Shadow
    @Final
    private ContainerData dataAccess;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void replaceDataAccess(BlockPos pos, BlockState state, CallbackInfo ci) {
        try {
            Field field = BeaconBlockEntity.class.getDeclaredField("dataAccess");
            field.setAccessible(true);

            field.set(this, new ContainerData() {

                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 -> levels;
                        case 1 -> BeaconMenu.encodeEffect(primaryPower);
                        case 2 -> BeaconMenu.encodeEffect(secondaryPower);
                        case 3 -> BeaconBlockEntityMixin.this.getTier().ordinal();
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                    switch (index) {
                        case 0 -> levels = value;
                        case 1 -> primaryPower = BeaconBlockEntityInvoker.filterEffect(BeaconMenu.decodeEffect(value));
                        case 2 -> secondaryPower = BeaconBlockEntityInvoker.filterEffect(BeaconMenu.decodeEffect(value));
                        case 3 -> BeaconBlockEntityMixin.this
                                .setTier(PotencyTier.values()[value]);
                    }
                }

                @Override
                public int getCount() {
                    return 3 + 1;
                }
            });

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to replace dataAccess", e);
        }
    }

}
