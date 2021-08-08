package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.StakeItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nonnull;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;

public class StakeCondition implements LootItemCondition {
    public static Builder builder(LootContext.EntityTarget target) {
        return () -> new StakeCondition(target);
    }
    private final LootContext.EntityTarget target;

    public StakeCondition(LootContext.EntityTarget targetIn) {
        this.target = targetIn;
    }

    @Nonnull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.with_stake;
    }

    @Override
    public boolean test(LootContext context) {
        Entity player = context.getParamOrNull(target.getParam());
        if (player instanceof Player) {
            ItemStack active = ((Player) player).getMainHandItem();
            return !active.isEmpty() && active.getItem() instanceof StakeItem;
        }
        return false;
    }

    public static class Serializer implements Serializer<StakeCondition> {


        @Nonnull
        @Override
        public StakeCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new StakeCondition(GsonHelper.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, StakeCondition value, JsonSerializationContext context) {
            json.add("entity", context.serialize(value.target));

        }


    }
}
