package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends ForgeRegistryEntry<IMinionTask<?, ?>> implements IMinionTask<T, Q> {
    private Component name;

    @Nullable
    @Override
    public T activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data) {
        triggerAdvancements(lord);
        return null;
    }

    @Override
    public Component getName() {
        if (name == null) {
            name = new TranslatableComponent(Util.makeDescriptionId("minion_task", getRegistryName()));
        }
        return name;
    }

    protected void triggerAdvancements(Player player) {
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_MINION_ACTION.trigger(((ServerPlayer) player), this);
        }
    }
}
