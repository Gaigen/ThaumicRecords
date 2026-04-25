package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneWorkbenchBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneWorkbenchBlock> CODEC = simpleCodec(ArcaneWorkbenchBlock::new);

    public ArcaneWorkbenchBlock(Properties properties) {
        super(properties);
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
}
