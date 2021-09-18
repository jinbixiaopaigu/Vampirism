package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class HunterTaskMasterRenderer extends MobRenderer<HunterTaskMasterEntity, VillagerModel<HunterTaskMasterEntity>> {
    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_task_master_overlay.png");

    public HunterTaskMasterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
//        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
        this.addLayer(new HunterEquipmentLayer<>(this, context.getModelSet(), h -> HunterEquipmentModel.StakeType.NONE, h -> HunterEquipmentModel.HatType.HAT2));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull HunterTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(@Nonnull HunterTaskMasterEntity entityIn, @Nonnull Component displayNameIn, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }


}