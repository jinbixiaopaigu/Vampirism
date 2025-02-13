package de.teamlapen.lib.lib.entity;


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;

/**
 * Provides several event related methods, which should be called by a dedicated EventHandler.
 * You can register a {@link Capability}, which instances implement this interface, in {@link de.teamlapen.lib.HelperRegistry} to let the library call this.
 */
public interface IPlayerEventListener {

    void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to);

    void onDeath(DamageSource src);

    /**
     * Called when the corresponding player is attacked.
     *
     * @return If true the damage will be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);

    /**
     * Called when the player killed a living entity
     *
     * @param victim The killed entity
     * @param src    The lethal damage source
     */
    default void onEntityKilled(LivingEntity victim, DamageSource src) {
    }

    void onJoinWorld();

    void onPlayerClone(Player original, boolean wasDeath);

    void onPlayerLoggedIn();

    void onPlayerLoggedOut();

    /**
     * Called during EntityLiving Update. Somewhere in the middle of {@link Player}'s onUpdate
     */
    void onUpdate();

    /**
     * Called at the beginning and at the end of {@link Player}'s onUpdate. {@link IPlayerEventListener#onUpdate()} is called in between.
     * Should only be used for stuff that requires to run at the beginning or end
     */
    void onUpdatePlayer(TickEvent.Phase phase);
}
