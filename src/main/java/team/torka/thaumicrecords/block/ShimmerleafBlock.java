package team.torka.thaumicrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.torka.thaumicrecords.client.particle.SparkleParticle;

public class ShimmerleafBlock extends FlowerBlock {
    public ShimmerleafBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
        super(effect, seconds, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(2) == 0) {
            VoxelShape shape = state.getShape(level, pos);
            if (!shape.isEmpty()) {
                AABB bounds = shape.bounds();
                double x = pos.getX() + bounds.minX + random.nextDouble() * (bounds.maxX - bounds.minX);
                double y = pos.getY() + (bounds.maxY - bounds.minY) / 2 + random.nextDouble() * (bounds.maxY - bounds.minY) / 2;
                double z = pos.getZ() + bounds.minZ + random.nextDouble() * (bounds.maxZ - bounds.minZ);
                SparkleParticle sparkle = SparkleParticle.createDirect((net.minecraft.client.multiplayer.ClientLevel) level, x, y, z, 0f, 0.03f, 0f, 1.5F,
                        0xff09a7eb, 6);
                sparkle.setNoClip(true);
                net.minecraft.client.Minecraft.getInstance().particleEngine.add(sparkle);
            }
        }
    }
}
