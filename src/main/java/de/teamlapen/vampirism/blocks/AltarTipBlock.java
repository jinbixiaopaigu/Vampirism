package de.teamlapen.vampirism.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Part of the Altar of Infusion structure
 */
public class AltarTipBlock extends VampirismBlock {
    protected static final VoxelShape tipShape = makeShape();
    private final static String name = "altar_tip";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(3, 0, 3, 13, 3, 13);
        VoxelShape b = Block.box(4, 3, 4, 12, 4, 12);
        VoxelShape c = Block.box(5, 4, 5, 11, 5, 11);
        VoxelShape d = Block.box(6, 5, 6, 10, 6, 10);
        VoxelShape e = Block.box(7, 6, 7, 9, 7, 9);
        return Shapes.or(a, b, c, d, e);
    }

    public AltarTipBlock() {
        super(name, Properties.of(Material.METAL).strength(1f).noOcclusion());
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return tipShape;
    }


}
