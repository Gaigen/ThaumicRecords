package team.torka.thaumicrecords.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.item.ScribingTool;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.block.entity.TableBlockEntity;
import team.torka.thaumicrecords.block.part.ResearchTablePart;
import team.torka.thaumicrecords.menu.ArcaneWorkbenchMenu;
import team.torka.thaumicrecords.menu.ResearchTableMenu;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class TableBlock extends BaseEntityBlock {
    public static final MapCodec<TableBlock> CODEC = simpleCodec(TableBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 12, 0, 16, 16, 16), Block.box(10, 4, 6, 14, 12, 10), Block.box(2, 4, 6, 6, 12, 10),
            Block.box(0, 0, 4, 16, 4, 12));
    private static final Map<Direction, VoxelShape> DIRECTIONAL_SHAPE = ImmutableMap.of(Direction.NORTH, SHAPE, Direction.SOUTH,
            calculateRotation(Direction.SOUTH), Direction.EAST, calculateRotation(Direction.EAST), Direction.WEST, calculateRotation(Direction.WEST));
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape calculateRotation(Direction targetDirection) {
        VoxelShape[] buffer = new VoxelShape[]{Shapes.empty()};
        TableBlock.SHAPE.toAabbs().forEach(aabb -> {
            double minX = aabb.minX * 16.0D;
            double minY = aabb.minY * 16.0D;
            double minZ = aabb.minZ * 16.0D;
            double maxX = aabb.maxX * 16.0D;
            double maxY = aabb.maxY * 16.0D;
            double maxZ = aabb.maxZ * 16.0D;
            VoxelShape rotatedCube;
            switch (targetDirection) {
                case SOUTH -> rotatedCube = Block.box(16.0D - maxX, minY, 16.0D - maxZ, 16.0D - minX, maxY, 16.0D - minZ);
                case WEST -> rotatedCube = Block.box(minZ, minY, 16.0D - maxX, maxZ, maxY, 16.0D - minX);
                case EAST -> rotatedCube = Block.box(16.0D - maxZ, minY, minX, 16.0D - minZ, maxY, maxX);
                default -> rotatedCube = Block.box(minX, minY, minZ, maxX, maxY, maxZ);
            }
            buffer[0] = Shapes.or(buffer[0], rotatedCube);
        });

        return buffer[0].optimize();
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
        Direction direction = state.getValue(FACING);
        return DIRECTIONAL_SHAPE.getOrDefault(direction, SHAPE);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        return DIRECTIONAL_SHAPE.getOrDefault(direction, SHAPE);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                           BlockHitResult hitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        // 法杖，转化为奥术工作台
        if (stack.is(ItemRegistry.WAND)) {
            if (!level.isClientSide) {
                level.setBlock(pos, BlockRegistry.ARCANE_WORKBENCH.get().defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ArcaneWorkbenchBlockEntity workbenchBE) {
                    workbenchBE.getInventory().setStackInSlot(ArcaneWorkbenchMenu.SLOT_WAND, itemstack.copy());
                    itemstack.setCount(0);
                    level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        // 笔与墨，转化为研究台
        if (itemstack.getItem() instanceof ScribingTool) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (neighborState.is(BlockRegistry.TABLE.get())) {
                    if (!level.isClientSide) {
                        this.convertToResearchTable(level, pos, neighborPos, player, stack, dir);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    private void convertToResearchTable(Level level, BlockPos pos1, BlockPos pos2, Player player, ItemStack stack, Direction dir) {
        Direction tableFacing = dir.getCounterClockWise();
        level.setBlock(pos1, BlockRegistry.RESEARCH_TABLE.get()
                .defaultBlockState()
                .setValue(ResearchTableBlock.FACING, tableFacing)
                .setValue(ResearchTableBlock.PART, ResearchTablePart.LEFT), 3);
        level.setBlock(pos2, BlockRegistry.RESEARCH_TABLE.get()
                .defaultBlockState()
                .setValue(ResearchTableBlock.FACING, tableFacing)
                .setValue(ResearchTableBlock.PART, ResearchTablePart.RIGHT), 3);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        BlockEntity be = level.getBlockEntity(pos1);
        if (be instanceof ResearchTableBlockEntity researchBE) {
            researchBE.getInventory().setStackInSlot(ResearchTableMenu.SLOT_SCRIBE_TOOLS, stack.copy());
            stack.setCount(0);
        }

    }
}