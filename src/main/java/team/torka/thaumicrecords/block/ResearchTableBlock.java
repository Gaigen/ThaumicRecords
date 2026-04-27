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
import net.minecraft.world.phys.shapes.Shapes;
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

    private static final VoxelShape TOP_LEFT = Block.box(0, 12, 0, 16, 16, 16);
    private static final VoxelShape TOP_RIGHT = Block.box(0, 12, 0, 16, 16, 16);
    private static final VoxelShape LEGS_LEFT = Shapes.or(Block.box(2, 0, 2, 6, 12, 6), Block.box(2, 0, 10, 6, 12, 14));
    private static final VoxelShape LEGS_RIGHT = Shapes.or(Block.box(10, 0, 2, 14, 12, 6), Block.box(10, 0, 10, 14, 12, 14));
    private static final VoxelShape CROSSBAR_LEFT = Block.box(4, 2, 6, 16, 6, 10);
    private static final VoxelShape CROSSBAR_RIGHT = Block.box(0, 2, 6, 12, 6, 10);
    private static final VoxelShape SHAPE_LEFT = Shapes.or(TOP_LEFT, LEGS_LEFT, CROSSBAR_LEFT);
    private static final VoxelShape SHAPE_RIGHT = Shapes.or(TOP_RIGHT, LEGS_RIGHT, CROSSBAR_RIGHT);

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
        Direction facing = state.getValue(FACING);
        ResearchTablePart part = state.getValue(PART);
        VoxelShape baseShape = (part == ResearchTablePart.LEFT) ? SHAPE_LEFT : SHAPE_RIGHT;
        return rotateShape(facing, baseShape);
    }

    public static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Block.box(16 * (1 - maxZ), 16 * minY, 16 * minX, 16 * (1 - minZ), 16 * maxY, 16 * maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
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