package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.ArcanePedestalBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcanePedestalBlock extends BaseEntityBlock {
    public static final MapCodec<ArcanePedestalBlock> CODEC = simpleCodec(ArcanePedestalBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(4, 4, 4, 12, 12, 12), Block.box(2, 12, 2, 14, 16, 14));

    public ArcanePedestalBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal) {
            IItemHandler handler = pedestal.getItemStackHandler();
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (pedestal.hasItem()) {
                if (!level.isClientSide) {
                    ItemStack extracted = handler.extractItem(0, 1, false);
                    if (!player.getInventory().add(extracted)) {
                        player.drop(extracted, false);
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (!heldItem.isEmpty()) {
                ItemStack singleItem = heldItem.copyWithCount(1);
                ItemStack remainder = handler.insertItem(0, singleItem, true);
                if (remainder.isEmpty()) {
                    if (!level.isClientSide) {
                        handler.insertItem(0, singleItem, false);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ArcanePedestalBlockEntity pedestal) {
                for (int i = 0; i < pedestal.getItemStackHandler().getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pedestal.getItemStackHandler().getStackInSlot(i));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcanePedestalBlockEntity(pos, state);
    }
}
