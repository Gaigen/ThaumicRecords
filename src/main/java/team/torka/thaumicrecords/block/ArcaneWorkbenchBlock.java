package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneWorkbenchBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneWorkbenchBlock> CODEC = simpleCodec(ArcaneWorkbenchBlock::new);
    private static final VoxelShape TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    private static final VoxelShape LEG1 = Block.box(11.0D, 4.0D, 1.0D, 15.0D, 8.0D, 5.0D);
    private static final VoxelShape LEG2 = Block.box(1.0D, 4.0D, 11.0D, 5.0D, 8.0D, 15.0D);
    private static final VoxelShape LEG3 = Block.box(11.0D, 4.0D, 11.0D, 15.0D, 8.0D, 15.0D);
    private static final VoxelShape LEG4 = Block.box(1.0D, 4.0D, 1.0D, 5.0D, 8.0D, 5.0D);

    private static final VoxelShape SHAPE = Shapes.or(TOP, BASE, LEG1, LEG2, LEG3, LEG4);

    public ArcaneWorkbenchBlock(Properties properties) {
        super(properties);
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

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneWorkbenchBlockEntity(pos, state);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ArcaneWorkbenchBlockEntity workbench) {
                for (int i = 0; i < workbench.getInventory().getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), workbench.getInventory().getStackInSlot(i));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
