package team.torka.thaumicrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Finite fluid block — port of Forge 1.7.10 BlockFluidFinite / BlockFluidClassic.
 * Quantum-based spreading using scheduled ticks.
 * <p>
 * Block LEVEL 0 = 16 quanta (full), LEVEL 15 = 1 quanta.
 */
public class FiniteFluidBlock extends Block implements BucketPickup {

    public static final int Q = 16;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    private static final VoxelShape STABLE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected final Supplier<FiniteFluid> fluidSupplier;

    public FiniteFluidBlock(Supplier<FiniteFluid> fluid, BlockBehaviour.Properties props) {
        super(props);
        this.fluidSupplier = fluid;
        registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    protected FiniteFluid getFiniteFluid() {
        return fluidSupplier.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    // --- FluidState mapping ---

    @Override
    protected FluidState getFluidState(BlockState state) {
        int blockLevel = state.getValue(LEVEL);
        int amount = Q - blockLevel;
        return getFiniteFluid().defaultFluidState().setValue(FiniteFluid.FLUID_LEVEL, amount);
    }

    // --- Rendering properties ---

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (ctx.isAbove(STABLE_SHAPE, pos, true) && state.getValue(LEVEL) == 0 && ctx.canStandOnFluid(level.getFluidState(pos.above()),
                state.getFluidState())) {
            return STABLE_SHAPE;
        }
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return !state.getFluidState().is(FluidTags.LAVA);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighbor, Direction dir) {
        return neighbor.getFluidState().getType().isSame(getFiniteFluid());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.emptyList();
    }

    // --- Tick scheduling ---

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        scheduleFluidTick(level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (neighborState.isAir() || neighborState.is(this)) {
            scheduleFluidTick(level, pos);
        }
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        // No-op — onPlace and updateShape handle scheduling
    }

    private void scheduleFluidTick(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level lv) {
            BlockState state = lv.getBlockState(pos);
            if (!state.isAir()) {
                lv.scheduleTick(pos, this, getFiniteFluid().getTickDelay(lv));
            }
        }
    }

    // --- Quantum spreading (ported from BlockFluidFinite / BlockFluidClassic) ---

    @Override
    @ParametersAreNonnullByDefault
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        int quantaBefore = quantaOf(level, pos);
        if (quantaBefore <= 0) {
            level.removeBlock(pos, false);
            return;
        }

        // 1. Try to flow down
        int afterDown = tryFlowDown(level, pos, quantaBefore);
        if (afterDown <= 0) {
            return;
        }

        // 2. Always spread horizontally with remaining quanta (TC4 original)
        spreadHorizontally(level, pos, afterDown, random);

        // 3. Reschedule if quanta changed
        if (level.getBlockState(pos).is(this)) {
            int quantaAfter = quantaOf(level, pos);
            if (quantaAfter > 0 && quantaAfter != quantaBefore) {
                level.scheduleTick(pos, this, getFiniteFluid().getTickDelay(level));
            }
        }
    }

    protected int quantaOf(LevelAccessor world, BlockPos pos) {
        BlockState st = world.getBlockState(pos);
        if (st.isAir()) {
            return 0;
        }
        if (!st.is(this)) {
            return -1;
        }
        return Q - Math.min(st.getValue(LEVEL), Q - 1);
    }

    private int tryFlowDown(ServerLevel level, BlockPos pos, int quanta) {
        BlockPos below = pos.below();
        if (below.getY() < level.getMinBuildHeight()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            return 0;
        }

        int belowQuanta = quantaOf(level, below);
        if (belowQuanta >= 0) {
            int total = belowQuanta + quanta;
            if (total > Q) {
                // Below would overflow — keep excess at current
                setQuanta(level, below, Q);
                setQuanta(level, pos, total - Q);
                return total - Q;
            }
            // All fits below — merge and move
            setQuanta(level, below, total);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            return 0;
        }

        BlockState belowState = level.getBlockState(below);
        if (canDisplace(level, below, belowState)) {
            if (!belowState.isAir()) {
                Block.dropResources(belowState, level, below);
            }
            setQuanta(level, below, quanta);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            return 0;
        }

        return quanta;
    }

    private void spreadHorizontally(ServerLevel level, BlockPos pos, int quanta, RandomSource random) {
        int lowerThan = quanta;
        int total = quanta;
        int count = 1;

        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(side);
            int nq = quantaBelow(level, neighbor, lowerThan);
            if (nq >= 0) {
                count++;
                total += nq;
            }
        }

        if (count <= 1) {
            return;
        }

        int each = total / count;
        int rem = total % count;

        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(side);
            int nq = quantaBelow(level, neighbor, lowerThan);
            if (nq >= 0) {
                int newQ = each;
                if (newQ != nq) {
                    setQuanta(level, neighbor, newQ);
                }
                count--;
            }
        }

        int selfQ = each + rem;
        setQuanta(level, pos, selfQ);
    }

    private int quantaBelow(LevelAccessor world, BlockPos pos, int belowThis) {
        int q = quantaOf(world, pos);
        if (q < 0) {
            return -1; // solid block
        }
        if (q == 0) {
            return 0; // air — always valid target
        }
        return q < belowThis ? q : -1;
    }

    protected void setQuanta(LevelAccessor world, BlockPos pos, int quanta) {
        if (quanta <= 0) {
            if (world instanceof Level lv) {
                lv.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        } else {
            int clamped = Math.min(quanta, Q);
            int blockLevel = Q - clamped;
            world.setBlock(pos, defaultBlockState().setValue(LEVEL, blockLevel), 3);
        }
    }

    protected boolean canDisplace(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return true;
        }
        if (state.is(this)) {
            return false;
        }
        if (state.blocksMotion()) {
            return false;
        }
        return !state.liquid();
    }

    // --- Entity collision (override point) ---

    @Override
    @ParametersAreNonnullByDefault
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    // --- Random tick (disabled — we use scheduled ticks) ---

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    // --- Bucket pickup ---

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(LEVEL) == 0) {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 11);
            return new ItemStack(getFiniteFluid().getBucket());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
