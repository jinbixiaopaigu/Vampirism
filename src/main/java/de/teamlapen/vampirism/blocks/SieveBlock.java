package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.blockentity.SieveBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

public class SieveBlock extends VampirismBlockContainer {

    public static final BooleanProperty PROPERTY_ACTIVE = BooleanProperty.create("active");
    protected static final VoxelShape sieveShape = makeShape();
    private final static String regName = "blood_sieve";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.box(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.box(5, 2, 5, 11, 12, 11);
        VoxelShape d = Block.box(3, 6, 3, 13, 9, 13);
        VoxelShape e = Block.box(1, 12, 1, 15, 14, 15);
        VoxelShape f = Block.box(0, 14, 0, 16, 16, 16);

        return Shapes.or(a, b, c, d, e, f);
    }

    public SieveBlock() {
        super(regName, Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(PROPERTY_ACTIVE, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SieveBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return sieveShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ACTIVE);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.sieve, SieveBlockEntity::tick);
    }
}
