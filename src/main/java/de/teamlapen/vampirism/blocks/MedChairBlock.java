package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismHorizontalBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    private final static String name = "med_chair";
    private final VoxelShape SHAPE_TOP;
    private final VoxelShape SHAPE_BOTTOM;


    public MedChairBlock() {
        super(name, Properties.of(Material.METAL).strength(1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, EnumPart.TOP));
        SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
        SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);
    }


    @Nonnull
    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(ModItems.item_med_chair);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return state.getValue(PART) == EnumPart.BOTTOM ? SHAPE_BOTTOM : SHAPE_TOP;
    }

    @Override
    public void playerDestroy(@Nonnull Level worldIn, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity te, @Nonnull ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    @Override
    public void playerWillDestroy(@Nonnull Level worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull Player player) {
        EnumPart part = state.getValue(PART);
        BlockPos other;
        Direction dir = state.getValue(FACING);
        if (state.getValue(PART) == EnumPart.TOP) {
            other = pos.relative(dir);
        } else {
            other = pos.relative(dir.getOpposite());
        }
        BlockState otherState = worldIn.getBlockState(other);
        if (otherState.getBlock() == this && otherState.getValue(PART) != part) {
            worldIn.setBlock(other, Blocks.AIR.defaultBlockState(), 35);
            worldIn.levelEvent(player, 2001, other, Block.getId(otherState));
            if (!worldIn.isClientSide && !player.isCreative()) {
                ItemStack itemstack = player.getMainHandItem();
                dropResources(state, worldIn, pos, null, player, itemstack);
                dropResources(otherState, worldIn, other, null, player, itemstack);
            }
            player.awardStat(Stats.BLOCK_MINED.get(this));
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getItemInHand(hand);
            if (handleInjections(player, world, stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.removeItem(stack);
                }
            }
        } else if (world.isClientSide) {
            player.displayClientMessage(new TranslatableComponent("text.vampirism.need_item_to_use", new TranslatableComponent((new ItemStack(ModItems.injection_garlic).getDescriptionId()))), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    private boolean handleGarlicInjection(@Nonnull Player player, @Nonnull Level world, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isClientSide) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addEffect(new MobEffectInstance(ModEffects.poison, 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isClientSide) {
                player.sendMessage(new TranslatableComponent("text.vampirism.med_chair_other_faction", currentFaction.getName()), Util.NIL_UUID);
            }
        }
        return false;
    }

    private boolean handleInjections(Player player, Level world, ItemStack stack) {
        IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
        IPlayableFaction<?> faction = handler.getCurrentFaction();
        if (stack.getItem().equals(ModItems.injection_garlic)) {
            return handleGarlicInjection(player, world, handler, faction);
        }
        if (stack.getItem().equals(ModItems.injection_sanguinare)) {
            return handleSanguinareInjection(player, handler, faction);
        }
        if (stack.getItem().equals(ModItems.injection_zombie_blood)) {
            return handleZombieBloodInjection(player);
        }
        return false;
    }

    private boolean handleSanguinareInjection(@Nonnull Player player, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.displayClientMessage(new TranslatableComponent("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return true;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(new TranslatableComponent("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareEffect.addRandom(player, true);
                    player.addEffect(new MobEffectInstance(ModEffects.poison, 60));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleZombieBloodInjection(@Nonnull Player player) {
        player.addEffect(new MobEffectInstance(ModEffects.poison, 200));
        return true;
    }


    public enum EnumPart implements StringRepresentable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public static EnumPart fromMeta(int meta) {
            if (meta == 1) {
                return BOTTOM;
            }
            return TOP;
        }

        public final String name;
        public final int meta;

        EnumPart(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return getSerializedName();
        }


    }
}
