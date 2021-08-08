package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.advancements.CriterionTrigger.Listener;

/**
 * Implements some general function used in most criterion triggers.
 * The concept is more or less copied from vanilla.
 * <p>
 * It is quite complex/strange but I guess MC has it's reasons
 *
 * @param <T>
 */
@Deprecated
public abstract class AbstractCriterionTrigger<T extends CriterionTriggerInstance> implements CriterionTrigger<T> { //TODO 1.17 remove

    protected final Map<PlayerAdvancements, GenericListeners<T>> listenersForPlayers = Maps.newHashMap();
    private final ResourceLocation id;
    private final Function<PlayerAdvancements, GenericListeners<T>> listenerConstructor;


    public AbstractCriterionTrigger(ResourceLocation id, Function<PlayerAdvancements, GenericListeners<T>> listenerConstructor) {
        this.id = id;
        this.listenerConstructor = listenerConstructor;
    }

    @Override
    public void addPlayerListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<T> listener) {
        GenericListeners<T> listeners = this.listenersForPlayers.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = listenerConstructor.apply(playerAdvancementsIn);
            this.listenersForPlayers.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void removePlayerListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<T> listener) {
        GenericListeners<T> listeners = this.listenersForPlayers.get(playerAdvancementsIn);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.listenersForPlayers.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removePlayerListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
        this.listenersForPlayers.remove(playerAdvancementsIn);
    }

    protected abstract static class GenericListeners<T extends CriterionTriggerInstance> {
        protected final PlayerAdvancements playerAdvancements;
        protected final Set<Listener<T>> playerListeners = Sets.newHashSet();

        public GenericListeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public void add(CriterionTrigger.Listener<T> listener) {
            this.playerListeners.add(listener);
        }

        public boolean isEmpty() {
            return this.playerListeners.isEmpty();
        }

        public void remove(CriterionTrigger.Listener<T> listener) {
            this.playerListeners.remove(listener);
        }
    }
}
