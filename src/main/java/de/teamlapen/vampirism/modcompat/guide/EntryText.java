package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.entry.EntryResourceLocation;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Simple bullet point text entry
 */
public class EntryText extends EntryResourceLocation {
    public EntryText(List<IPage> pageList, Component name) {
        super(pageList, name, new ResourceLocation(REFERENCE.MODID, "textures/item/vampire_fang.png"));
    }

}
