package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.render.entities.ConvertedCreatureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the vampire overlay
 */
@OnlyIn(Dist.CLIENT)
public class VampireEntityLayer<T extends PathfinderMob, U extends EntityModel<T>> extends RenderLayer<T, U> {

    private final ResourceLocation overlay;
    private final boolean checkIfRender;

    /**
     * @param overlay
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public VampireEntityLayer(RenderLayerParent<T, U> entityRendererIn, ResourceLocation overlay, boolean checkIfRender) {
        super(entityRendererIn);
        this.overlay = overlay;
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!entity.isInvisible() && (!checkIfRender || ConvertedCreatureRenderer.renderOverlay)) {
            renderColoredCutoutModel(this.getParentModel(), overlay, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
        }
    }
}
