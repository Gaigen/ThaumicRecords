package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.DeconstructionTableBlockEntity;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class DeconstructionTableBlock extends BaseEntityBlock {

    public static final MapCodec<DeconstructionTableBlock> CODEC = simpleCodec(DeconstructionTableBlock::new);

    public DeconstructionTableBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
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
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeconstructionTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, BlockEntityRegistry.DECONSTRUCTION_TABLE.get(), DeconstructionTableBlockEntity::serverTick);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DeconstructionTableBlockEntity decon) {
                player.openMenu(new SimpleMenuProvider((id, inv, p) -> new team.torka.thaumicrecords.menu.DeconstructionTableMenu(id, inv, decon),
                        Component.translatable("container.thaumicrecords.deconstruction_table")), pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DeconstructionTableBlockEntity decon) {
                decon.dropInventory();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    @ParametersAreNonnullByDefault
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider) {
            return provider;
        }
        return null;
    }
}
