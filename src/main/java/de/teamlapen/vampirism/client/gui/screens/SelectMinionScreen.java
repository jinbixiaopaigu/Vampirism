package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.ScrollableArrayTextComponentList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ClientboundRequestMinionSelectPacket;
import de.teamlapen.vampirism.network.ServerboundSelectMinionTaskPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class SelectMinionScreen extends Screen {
    private final Integer @NotNull [] minionIds;
    private final Component @NotNull [] minionNames;
    private final ClientboundRequestMinionSelectPacket.Action action;
    private ScrollableArrayTextComponentList list;

    public SelectMinionScreen(ClientboundRequestMinionSelectPacket.Action a, @NotNull List<Pair<Integer, Component>> minions) {
        super(Component.literal(""));
        this.action = a;
        this.minionIds = minions.stream().map(Pair::getLeft).toArray(Integer[]::new);
        this.minionNames = minions.stream().map(Pair::getRight).toArray(Component[]::new);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    @Override
    public void render(@NotNull PoseStack mStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground(mStack);
        super.render(mStack, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    protected void init() {
        super.init();

        int w = 100;
        int maxH = 5;
        this.list = this.addRenderableWidget(new ScrollableArrayTextComponentList((this.width - w) / 2, (this.height - maxH * 20) / 2, w, Math.min(maxH * 20, 20 * minionNames.length), 20, () -> this.minionNames, SelectMinionScreen.this::onMinionSelected));
    }

    private void onMinionSelected(int id) {
        int selectedMinion = minionIds[id];
        if (action == ClientboundRequestMinionSelectPacket.Action.CALL) {
            VampirismMod.dispatcher.sendToServer(new ServerboundSelectMinionTaskPacket(selectedMinion, ServerboundSelectMinionTaskPacket.RECALL));
        }
        this.onClose();
    }
}