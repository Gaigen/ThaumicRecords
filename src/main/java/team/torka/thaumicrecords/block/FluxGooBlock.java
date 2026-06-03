package team.torka.thaumicrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import team.torka.thaumicrecords.registry.ParticleRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * Flux Goo — TC4 BlockFluxGoo port.
 * <p>
 * TC4-specific behavior on top of FiniteFluidBlock:
 * - Entity slowdown proportional to fill level
 * - Slowness potion effect (placeholder for VisExhaust)
 * - Slime spawning (TODO: needs EntityThaumicSlime)
 * - Taint conversion (TODO: needs taint system)
 * - Flux gas emission (TODO: needs flux gas block)
 * - Evaporation at low levels
 * - Bubble particles
 * - Replaceable at low levels (meta < 2 → level >= 14)
 */
public class FluxGooBlock extends FiniteFluidBlock {

    public FluxGooBlock(Supplier<FiniteFluid> fluid) {
        super(fluid, Properties.of()
                .replaceable()
                .strength(100.0F, 100.0F)
                .lightLevel(state -> 7)
                .pushReaction(PushReaction.BLOCK)
                .noLootTable()
                .sound(new SoundType(0.5F, 0.8F, SoundRegistry.GORE.get(), SoundRegistry.GORE.get(), SoundRegistry.GORE.get(), SoundRegistry.GORE.get(),
                        SoundRegistry.GORE.get())));
    }

    // --- Entity collision (TC4 onEntityCollidedWithBlock) ---

    @Override
    @ParametersAreNonnullByDefault
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }

        int meta = state.getValue(LEVEL);

        // TODO: EntityThaumicSlime interaction
        // Original TC4:
        // if (entity instanceof EntityThaumicSlime slime) {
        //     if (slime.getSlimeSize() < meta && level.random.nextBoolean()) {
        //         slime.setSlimeSize(slime.getSlimeSize() + 1);
        //         if (meta > 1) {
        //             level.setBlock(pos, state.setValue(LEVEL, meta - 1), 3);
        //         } else {
        //             level.removeBlock(pos, false);
        //         }
        //     }
        //     return;
        // }

        // Slow entity motion by quanta percentage (TC4 original)
        float quantaPercent = (float) quantaOf(level, pos) / (float) Q;
        entity.setDeltaMovement(entity.getDeltaMovement().x * (1.0F - quantaPercent), entity.getDeltaMovement().y,
                entity.getDeltaMovement().z * (1.0F - quantaPercent));

        // Apply potion effect
        // TC4: new PotionEffect(Config.potionVisExhaustID, 600, meta / 3, true)
        if (entity instanceof LivingEntity living) {
            // TODO: replace with VisExhaust when custom potions are ported
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, meta / 3, true, true));
        }
    }

    // --- Scheduled tick (TC4 updateTick) ---

    @Override
    @ParametersAreNonnullByDefault
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 1. Finite fluid spreading (parent handles quantum flow)
        super.tick(state, level, pos, random);

        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        int meta = level.getBlockState(pos).getValue(LEVEL);
        int quanta = Q - meta;

        // 2. Slime spawning (TC4 original)
        // TODO: needs EntityThaumicSlime — see commented code above

        // 3. Evaporation (TC4 original: rand.nextInt(30) == 0)
        boolean evaporated = false;
        if (random.nextInt(30) == 0) {
            if (quanta <= 1) {
                level.removeBlock(pos, false);
                return;
            } else {
                setQuanta(level, pos, quanta - 1);
                evaporated = true;
                // 50% chance to spawn flux gas above
                if (random.nextBoolean() && level.isEmptyBlock(pos.above())) {
                    // TODO: place flux gas block
                }
            }
        }

        // 4. Reschedule if evaporation changed quanta
        if (evaporated && level.getBlockState(pos).is(this)) {
            level.scheduleTick(pos, this, getFiniteFluid().getTickDelay(level));
        }
    }

    // --- Particle effects (TC4 randomDisplayTick) ---

    @Override
    @ParametersAreNonnullByDefault
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int meta = state.getValue(LEVEL);
        int quanta = Q - meta;

        // TC4: rand.nextInt(50 - particleCount) <= meta
        if (random.nextInt(50) <= quanta) {
            double x = pos.getX() + random.nextFloat();
            double y = pos.getY() + 0.125D * ((double) (quanta - 1) / Q);
            double z = pos.getZ() + random.nextFloat();
            level.addParticle(ParticleRegistry.FLUX_GOO_BUBBLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    // --- Replaceable at low levels (TC4: isReplaceable when meta < 2) ---

    @Override
    @SuppressWarnings("deprecation")
    protected boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext ctx) {
        return true;
    }
}
