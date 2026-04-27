package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.block.part.ResearchTablePart;

import javax.annotation.ParametersAreNonnullByDefault;

public class ResearchTableBlock extends BaseEntityBlock {
    public static final MapCodec<ResearchTableBlock> CODEC = simpleCodec(ResearchTableBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ResearchTablePart> PART = EnumProperty.create("part", ResearchTablePart.class);

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public ResearchTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, ResearchTablePart.LEFT));
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockPos getMainPos(BlockState state, BlockPos pos) {
        if (state.getValue(PART) == ResearchTablePart.LEFT) {
            return pos;
        } else {
            return pos.relative(state.getValue(FACING).getCounterClockWise());
        }
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos mainPos = getMainPos(state, pos);
        BlockEntity be = level.getBlockEntity(mainPos);

        return InteractionResult.PASS;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction joiningDir = getNeighbourDirection(state.getValue(PART), state.getValue(FACING));
        if (direction == joiningDir && !neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static Direction getNeighbourDirection(ResearchTablePart part, Direction facing) {
        return part == ResearchTablePart.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.getAbilities().instabuild) {
            BlockPos otherPos = pos.relative(getNeighbourDirection(state.getValue(PART), state.getValue(FACING)));
            if (level.getBlockState(otherPos).is(this)) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == ResearchTablePart.LEFT ? new ResearchTableBlockEntity(pos, state) : null;
    }
}