package de.teamlapen.vampirism.api;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Internal handler for client class access in API
 */
class ClientHelper {

    /**
     * @return The client world if it matches the given dimension key
     */
    @Nullable
    static Level getAndCheckWorld(ResourceKey<Level> dimension) {
        Level clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            if (clientWorld.dimension().equals(dimension)) {
                return clientWorld;
            }
        }
        return null;
    }
}
