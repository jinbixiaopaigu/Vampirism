package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.level.material.Fluid;

public abstract class VampirismFluid extends Fluid {
    private final String fluidname;

    VampirismFluid(String name) {
        this.setRegistryName(REFERENCE.MODID, name);
        this.fluidname = name;
    }
}
