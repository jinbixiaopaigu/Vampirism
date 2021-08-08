package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.effect.MobEffectInstance;

public class IgnoreSunDamageEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public IgnoreSunDamageEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
        entity.addEffect(new MobEffectInstance(ModEffects.sunscreen, getDuration(entity.getLevel()), 0));

    }

    @Override
    public void deactivate(T entity) {
        if (entity.getEffect(ModEffects.sunscreen) != null && entity.getEffect(ModEffects.sunscreen).getAmplifier() == 0) {
            entity.removeEffect(ModEffects.sunscreen);
        }
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaIgnoreSundamageCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaIgnoreSundamageDuration.get() * 20;
    }

    @Override
    public int getWeight(PathfinderMob entity) {
        if (!entity.getCommandSenderWorld().isDay() || entity.getCommandSenderWorld().isRaining()) {//Not perfectly accurate (the actual sundamage checks for celestial angle and also might exclude certain dimensions and biomes
            return 0;
        }
        return ((IVampire) entity).isGettingSundamage(entity.level) ? 3 : 1;
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }
}
