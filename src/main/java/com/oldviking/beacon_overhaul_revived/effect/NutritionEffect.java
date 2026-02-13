package com.oldviking.beacon_overhaul_revived.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class NutritionEffect extends MobEffect {
    public NutritionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xC75F79);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return ((100 >> amplifier) <= 0) || (duration % (100 >> amplifier) == 0);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player) {
            ((Player) livingEntity).getFoodData().eat(1, 0.0F);
        }

        return super.applyEffectTick(serverLevel, livingEntity, amplifier);
    }
}
