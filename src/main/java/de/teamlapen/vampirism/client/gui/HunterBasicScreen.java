package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HunterBasicScreen extends AbstractContainerScreen<HunterBasicContainer> {
    private static final ResourceLocation guiTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_basic.png");

    private Button buttonLevelup;
    private int missing = 0;
    private int timer = 0;

    public HunterBasicScreen(HunterBasicContainer inventorySlotsIn, Inventory playerInventory, Component name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void init() {
        super.init();

        Component name = new TranslatableComponent("text.vampirism.level_up");
        int wid = this.font.width(name) + 5;
        int i = (this.imageWidth - wid) / 2;
        int j = (this.height - this.imageHeight) / 2;
        addButton(buttonLevelup = new Button(this.leftPos + i, j + 50, wid, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.BASICHUNTERLEVELUP, ""));
            this.onClose();
        }));
        buttonLevelup.active = false;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);

    }

    @Override
    public void tick() {
        super.tick();
        timer = (timer + 1) % 10;
        if (timer == 0) {
            this.missing = menu.getMissingCount();
            this.buttonLevelup.active = missing == 0;
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(guiTexture);
        this.blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);

        Component text = null;
        if (missing == 0) {
            text = new TranslatableComponent("text.vampirism.basic_hunter.i_will_train_you");
        } else if (missing > 0) {
            text = new TranslatableComponent("text.vampirism.basic_hunter.pay_n_vampire_blood_more", missing);
        }
        if (text != null) {
            this.font.drawWordWrap(text, 50, 12, 120, 0);
        }
    }
}
