package de.teamlapen.vampirism.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffectInstance extends MobEffectInstance {

    public VampireNightVisionEffectInstance() {
        super(MobEffects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<>());
    }

    @Override
    public void applyEffect(LivingEntity entityIn) {
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ == this;
    }

    @Override
    public String getDescriptionId() {
        return "effect.vampirism.nightVision";
    }

    @Override
    public boolean isNoCounter() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public boolean tick(LivingEntity entityIn, Runnable p_76455_2_) {
        return true;
    }

    @Override
    public boolean update(MobEffectInstance other) {
        //Don't change anything
        return false;
    }
}
