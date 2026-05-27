package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.registry.BlockRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

// Exact port of TC4 blockAiry metadata 4 (warding barrier)
// Invisible, only collides with non-player mobs, disabled by redstone on warding stone
public class WardingBarrierBlock extends Block {

    public static final MapCodec<WardingBarrierBlock> CODEC = simpleCodec(WardingBarrierBlock::new);

    private static final VoxelShape FULL = Block.box(0, 0, 0, 16, 16, 16);

    public WardingBarrierBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc) {
            Entity entity = ecc.getEntity();
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                for (int dy = 1; dy <= 2; dy++) {
                    BlockPos below = pos.below(dy);
                    if (level.getBlockState(below).is(BlockRegistry.PAVING_STONE_OF_WARDING.get())) {
                        if (level instanceof Level lvl) {
                            return lvl.hasNeighborSignal(below) ? Shapes.empty() : FULL;
                        }
                        return FULL;
                    }
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return 0.0F;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }
}
