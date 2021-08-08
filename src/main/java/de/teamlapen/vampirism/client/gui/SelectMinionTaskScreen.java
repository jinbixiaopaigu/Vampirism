package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.GuiPieMenu;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.network.SelectMinionTaskPacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class SelectMinionTaskScreen extends GuiPieMenu<SelectMinionTaskScreen.Entry> {


    public SelectMinionTaskScreen() {
        super(Color.gray, new TranslatableComponent("text.vampirism.minion.give_order"));
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_SPACE) {
            if (Minecraft.getInstance().player.isAlive()) {
                IPlayableFaction<?> faction = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
                if (faction != null) {
                    Minecraft.getInstance().setScreen(new SelectActionScreen(faction.getColor(), false));
                }
            }
        }
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (ModKeys.getKeyBinding(ModKeys.KEY.MINION).matches(key, scancode) || ModKeys.getKeyBinding(ModKeys.KEY.ACTION).matches(key, scancode)) {
            this.onClose();
            if (getSelectedElement() >= 0) {
                this.onElementSelected(elements.get(getSelectedElement()));
            }
        }
        return false;
    }

    @Override
    protected ResourceLocation getIconLoc(Entry item) {
        return item.getIconLoc();
    }

    @Override
    protected KeyMapping getMenuKeyBinding() {
        return ModKeys.getKeyBinding(ModKeys.KEY.MINION);
    }

    @Override
    protected Component getName(Entry item) {
        return item.getText();
    }


    @Override
    protected void onElementSelected(Entry id) {
        id.onSelected(this);
    }

    @Override
    protected void onGuiInit() {
        this.elements.clear();
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fp -> elements.addAll(PlayerMinionController.getAvailableTasks(fp).stream().map(Entry::new).collect(Collectors.toList())));
        this.elements.add(new Entry(new TranslatableComponent("action.vampirism.cancel"), new ResourceLocation(REFERENCE.MODID, "textures/actions/cancel.png"), (GuiPieMenu::onClose)));
        this.elements.add(new Entry(new TranslatableComponent("text.vampirism.minion.call_single"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall_single.png"), (SelectMinionTaskScreen::callSingle)));
        this.elements.add(new Entry(new TranslatableComponent("text.vampirism.minion.call_all"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/recall.png"), (SelectMinionTaskScreen::callAll)));
        this.elements.add(new Entry(new TranslatableComponent("text.vampirism.minion.respawn"), new ResourceLocation(REFERENCE.MODID, "textures/minion_tasks/respawn.png"), (SelectMinionTaskScreen::callRespawn)));
    }

    private void callAll() {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, SelectMinionTaskPacket.RECALL));

    }

    private void callRespawn() {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, SelectMinionTaskPacket.RESPAWN));

    }

    private void callSingle() {
        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.SELECT_CALL_MINION, ""));
    }

    private void sendTask(IMinionTask<?, ?> task) {
        VampirismMod.dispatcher.sendToServer(new SelectMinionTaskPacket(-1, task.getRegistryName()));
    }

    public static class Entry {

        private final Component text;
        private final ResourceLocation loc;
        private final Consumer<SelectMinionTaskScreen> onSelected;

        public Entry(IMinionTask<?, ?> task) {
            this(task.getName(), new ResourceLocation(task.getRegistryName().getNamespace(), "textures/minion_tasks/" + task.getRegistryName().getPath() + ".png"), (screen -> screen.sendTask(task)));
        }

        public Entry(Component text, ResourceLocation icon, Consumer<SelectMinionTaskScreen> onSelected) {
            this.text = text;
            this.loc = icon;
            this.onSelected = onSelected;
        }

        public ResourceLocation getIconLoc() {
            return loc;
        }

        public Component getText() {
            return text;
        }

        public void onSelected(SelectMinionTaskScreen screen) {
            this.onSelected.accept(screen);
        }

    }

}