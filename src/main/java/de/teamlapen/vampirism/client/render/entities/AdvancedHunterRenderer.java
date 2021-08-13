package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.layers.CloakLayer;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedHunterRenderer extends HumanoidMobRenderer<AdvancedHunterEntity, BasicHunterModel<AdvancedHunterEntity>> {
    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID + ":textures/entity/hunter_cloak.png");
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");


    public AdvancedHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, context.getModelSet(), h -> HunterEquipmentModel.StakeType.FULL, AdvancedHunterEntity::getHunterType));
        this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.getModel().head.visible = false;
            this.getModel().hat.visible = false;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AdvancedHunterEntity entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(AdvancedHunterEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 256) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }

}
