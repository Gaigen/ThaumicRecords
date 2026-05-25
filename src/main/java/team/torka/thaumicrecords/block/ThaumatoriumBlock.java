package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.ThaumatoriumBlockEntity;
import team.torka.thaumicrecords.block.part.ThaumatoriumPart;
import team.torka.thaumicrecords.menu.ThaumatoriumMenu;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ThaumatoriumBlock extends BaseEntityBlock {

    public static final MapCodec<ThaumatoriumBlock> CODEC = simpleCodec(ThaumatoriumBlock::new);
    public static final EnumProperty<ThaumatoriumPart> PART = EnumProperty.create("part", ThaumatoriumPart.class);

    private static final VoxelShape SHAPE_BOTTOM = Shapes.block();
    private static final VoxelShape SHAPE_TOP = Shapes.block();

    public ThaumatoriumBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, ThaumatoriumPart.BOTTOM));
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == ThaumatoriumPart.BOTTOM ? SHAPE_BOTTOM : SHAPE_TOP;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == ThaumatoriumPart.BOTTOM ? new ThaumatoriumBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (state.getValue(PART) != ThaumatoriumPart.BOTTOM) return null;
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, BlockEntityRegistry.THAUMATORIUM.get(),
                    (lvl, pos, st, be) -> ThaumatoriumBlockEntity.clientTick(lvl, pos, st, be));
        }
        return createTickerHelper(blockEntityType, BlockEntityRegistry.THAUMATORIUM.get(),
                (lvl, pos, st, be) -> ThaumatoriumBlockEntity.serverTick(lvl, pos, st, be));
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int type) {
        super.triggerEvent(state, level, pos, id, type);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, type);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ThaumatoriumBlockEntity thaum) {
                if (!thaum.inputStack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), thaum.inputStack);
                }
            }
            ThaumatoriumPart part = state.getValue(PART);
            BlockPos otherPos = part == ThaumatoriumPart.BOTTOM ? pos.above() : pos.below();
            if (level.getBlockState(otherPos).is(this)) {
                level.destroyBlock(otherPos, false);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockPos bottomPos = state.getValue(PART) == ThaumatoriumPart.BOTTOM ? pos : pos.below();
        BlockEntity be = level.getBlockEntity(bottomPos);
        if (be instanceof ThaumatoriumBlockEntity thaum) {
            // Empty hand + sneak = set facing
            if (player.isShiftKeyDown() && heldItem.isEmpty()) {
                thaum.facing = player.getDirection();
                thaum.syncToClient();
                thaum.setChanged();
                return ItemInteractionResult.SUCCESS;
            }

            player.openMenu(new SimpleMenuProvider((id, inv, p) -> new ThaumatoriumMenu(id, inv, thaum),
                    thaum.getDisplayName()), bottomPos);
        }
        return ItemInteractionResult.SUCCESS;
    }
}
