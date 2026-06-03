package team.torka.thaumicrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Finite fluid that does NOT auto-spread.
 * All spreading logic lives in FiniteFluidBlock.tick().
 * <p>
 * FLUID_LEVEL 1-16: 1 = 1 unit, 16 = full block.
 */
public class FiniteFluid extends Fluid {

    public static final int MAX_LEVEL = 16;
    public static final IntegerProperty FLUID_LEVEL = IntegerProperty.create("fluid_level", 1, MAX_LEVEL);

    private final Supplier<FluidType> fluidType;
    private final Supplier<? extends Item> bucket;
    private final Supplier<? extends FiniteFluidBlock> block;

    public FiniteFluid(Supplier<FluidType> fluidType, Supplier<? extends Item> bucket, Supplier<? extends FiniteFluidBlock> block) {
        this.fluidType = fluidType;
        this.bucket = bucket;
        this.block = block;
        registerDefaultState(this.stateDefinition.any().setValue(FLUID_LEVEL, MAX_LEVEL));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        builder.add(FLUID_LEVEL);
    }

    @Override
    public FluidType getFluidType() {
        return fluidType.get();
    }

    @Override
    public Item getBucket() {
        return bucket != null ? bucket.get() : Items.AIR;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
        return direction == Direction.DOWN && fluidIn != this;
    }

    @Override
    public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(net.minecraft.world.level.LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100F;
    }

    @Override
    public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
        if (level.getFluidState(pos.above()).getType() == this) {
            return 1.0F;
        }
        return getOwnHeight(state);
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return (float) getAmount(state) / (float) MAX_LEVEL;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        if (block != null) {
            int amount = getAmount(state);
            int blockLevel = MAX_LEVEL - amount;
            return block.get().defaultBlockState().setValue(FiniteFluidBlock.LEVEL, Math.min(blockLevel, 15));
        }
        return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return getAmount(state) == MAX_LEVEL;
    }

    @Override
    public int getAmount(FluidState state) {
        return state.getValue(FLUID_LEVEL);
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
        if (getAmount(state) >= MAX_LEVEL && level.getFluidState(pos.above()).getType() == this) {
            return Shapes.block();
        }
        return Shapes.box(0.0, 0.0, 0.0, 1.0, getHeight(state, level, pos), 1.0);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}
