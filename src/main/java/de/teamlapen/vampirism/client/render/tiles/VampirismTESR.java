package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * TESR with a few util methods
 */
@OnlyIn(Dist.CLIENT)
abstract class VampirismTESR<T extends BlockEntity> implements BlockEntityRenderer<T> {

    /**
     * Rotates the block to fit the enum facing.
     * ONLY CALL THIS IF THE BLOCK HAS A {@link HorizontalBlock#HORIZONTAL_FACING} PROPERTY
     *
     * @param tile
     */
    protected void adjustRotatePivotViaState(@Nullable BlockEntity tile, PoseStack matrixStack) {
        if (tile == null) return;
        Direction dir = Direction.NORTH;
        if (tile.getLevel() != null)
            dir = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((dir.get2DDataValue() - 2) * -90));
    }
}
