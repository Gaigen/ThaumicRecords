package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class InfusionMatrixBlock extends BaseEntityBlock {

    public static final MapCodec<InfusionMatrixBlock> CODEC = simpleCodec(InfusionMatrixBlock::new);

    public InfusionMatrixBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new InfusionMatrixBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                   @NotNull BlockEntityType<T> blockEntityType) {
        if (blockEntityType == BlockEntityRegistry.INFUSION_MATRIX.get()) {
            return InfusionMatrixBlockEntity::tick;
        }
        return null;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                           @NotNull CollisionContext context) {
        return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }
}
