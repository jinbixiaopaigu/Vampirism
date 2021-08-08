package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends DualBipedRenderer<VampireMinionEntity, PlayerModel<VampireMinionEntity>> {

    private final Pair<ResourceLocation, Boolean>[] textures;
    private final Pair<ResourceLocation, Boolean>[] minionSpecificTextures;


    public VampireMinionRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0F, false), new PlayerModel<>(0f, true), 0.5F);
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        textures = gatherTextures("textures/entity/vampire", true);
        minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);

        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(0.5f), new HumanoidModel<>(1f)));
        this.getModel().body.visible = this.getModel().jacket.visible = false;
        this.getModel().leftArm.visible = this.getModel().leftSleeve.visible = this.getModel().rightArm.visible = this.getModel().rightSleeve.visible = false;
        this.getModel().rightLeg.visible = this.getModel().rightPants.visible = this.getModel().leftLeg.visible = this.getModel().leftPants.visible = false;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    public int getVampireTextureCount() {
        return this.textures.length;
    }

    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(VampireMinionEntity entity) {
        Pair<ResourceLocation, Boolean> p = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
        if (entity.shouldRenderLordSkin()) {
            return entity.getOverlayPlayerProperties().map(Pair::getRight).map(b -> Pair.of(p.getLeft(), b)).orElse(p);
        }
        return p;
    }

    @Override
    protected void scale(VampireMinionEntity entityIn, PoseStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

}