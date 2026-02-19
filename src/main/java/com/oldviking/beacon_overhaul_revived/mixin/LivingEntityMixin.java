package com.oldviking.beacon_overhaul_revived.mixin;

import com.oldviking.beacon_overhaul_revived.BeaconOverhaulRevived;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private boolean stepIncreased;

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract @Nullable AttributeInstance getAttribute(Holder<Attribute> holder);

    @Inject(method = "maxUpStep()F", at = @At("HEAD"), require = 1)
    private void updateJumpBoostStepAssist(CallbackInfoReturnable<Float> cir) {
        if (this.hasEffect(MobEffects.JUMP_BOOST) && !this.isCrouching()) {
            if (!this.stepIncreased) {
                Objects.requireNonNull(this.getAttribute(Attributes.STEP_HEIGHT)).addOrUpdateTransientModifier(new  AttributeModifier(Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "step-up"), 1.0F, AttributeModifier.Operation.ADD_VALUE));
                this.stepIncreased = true;
            }
        } else if (this.stepIncreased) {
            Objects.requireNonNull(this.getAttribute(Attributes.STEP_HEIGHT)).removeModifier(Identifier.fromNamespaceAndPath(BeaconOverhaulRevived.MOD_ID, "step-up"));
            this.stepIncreased = false;
        }
    }

    @Inject(method = "getEffectiveGravity()D", at = @At("RETURN"), cancellable = true)
    private void dropIfCrouching(CallbackInfoReturnable<Double> cir) {
        if (this.isCrouching()) {
            cir.setReturnValue(this.getGravity());
        } else {
            cir.cancel();
        }
    }
}
