package team.torka.thaumicrecords.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import team.torka.thaumicrecords.registry.BlockRegistry;

public class SilverwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    public SilverwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        int minTreeHeight = 8;
        int randomTreeHeight = 5;
        int height = random.nextInt(randomTreeHeight) + minTreeHeight;
        if (pos.getY() < world.getMinBuildHeight() || pos.getY() + height + 1 > world.getMaxBuildHeight()) {
            return false;
        }
        for (int i1 = pos.getY(); i1 <= pos.getY() + 1 + height; ++i1) {
            int spread = (i1 == pos.getY()) ? 0 : (i1 >= pos.getY() + 1 + height - 2 ? 3 : 1);
            for (int j1 = pos.getX() - spread; j1 <= pos.getX() + spread; ++j1) {
                for (int k1 = pos.getZ() - spread; k1 <= pos.getZ() + spread; ++k1) {
                    if (i1 > pos.getY() && !world.getBlockState(new BlockPos(j1, i1, k1)).isAir()) {
                        if (!world.getBlockState(new BlockPos(j1, i1, k1)).canBeReplaced()) {
                            return false;
                        }
                    }
                }
            }
        }
        if (!world.getBlockState(pos.below()).is(BlockTags.DIRT)) {
            return false;
        }
        int start = pos.getY() + height - 5;
        int end = pos.getY() + height + 3 + random.nextInt(3);
        for (int k2 = start; k2 <= end; ++k2) {
            int cty = Mth.clamp(k2, pos.getY() + height - 3, pos.getY() + height);
            for (int xx = pos.getX() - 5; xx <= pos.getX() + 5; ++xx) {
                for (int zz = pos.getZ() - 5; zz <= pos.getZ() + 5; ++zz) {
                    double d3 = xx - pos.getX();
                    double d4 = k2 - cty;
                    double d5 = zz - pos.getZ();
                    double dist = d3 * d3 + d4 * d4 + d5 * d5;

                    if (dist < (10.0 + random.nextInt(8))) {
                        BlockPos leafPos = new BlockPos(xx, k2, zz);
                        if (world.getBlockState(leafPos).canBeReplaced()) {
                            world.setBlock(leafPos, BlockRegistry.SILVERWOOD_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1), 3);
                        }
                    }
                }
            }
        }
        int chance = (int) (height * 1.5F);
        boolean lastBlockWasNode = false;
        for (int v = 0; v < height; ++v) {
            BlockPos currentLogPos = pos.above(v);
            BlockState logState = BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState();
            if (v > 0 && !lastBlockWasNode && random.nextInt(Math.max(1, chance)) == 0) {
//                world.setBlock(currentLogPos, BlockRegistry.SILVERWOOD_LOG_NODE.get().defaultBlockState(), 3);
                world.setBlock(currentLogPos, BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
//                // TODO: 树节点
                chance += height;
                lastBlockWasNode = true;
            } else {
                world.setBlock(currentLogPos, logState, 3);
                lastBlockWasNode = false;
            }
            world.setBlock(currentLogPos.north(), logState, 3);
            world.setBlock(currentLogPos.south(), logState, 3);
            world.setBlock(currentLogPos.east(), logState, 3);
            world.setBlock(currentLogPos.west(), logState, 3);
        }

        world.setBlock(pos.offset(-1, 0, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(1, 0, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(-1, 0, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(1, 0, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);

        if (random.nextInt(3) != 0) {
            world.setBlock(pos.offset(-1, 1, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) != 0) {
            world.setBlock(pos.offset(1, 1, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) != 0) {
            world.setBlock(pos.offset(-1, 1, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) != 0) {
            world.setBlock(pos.offset(1, 1, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }

        placeHorizontalLog(world, pos.offset(-2, 0, 0), Direction.Axis.X);
        placeHorizontalLog(world, pos.offset(2, 0, 0), Direction.Axis.X);
        placeHorizontalLog(world, pos.offset(0, 0, -2), Direction.Axis.Z);
        placeHorizontalLog(world, pos.offset(0, 0, 2), Direction.Axis.Z);

        world.setBlock(pos.offset(-2, -1, 0), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(2, -1, 0), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(0, -1, -2), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(0, -1, 2), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);

        world.setBlock(pos.offset(-1, height - 4, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(1, height - 4, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(-1, height - 4, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(1, height - 4, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);

        if (random.nextInt(3) == 0) {
            world.setBlock(pos.offset(-1, height - 4 - 1, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) == 0) {
            world.setBlock(pos.offset(1, height - 4 - 1, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) == 0) {
            world.setBlock(pos.offset(-1, height - 4 - 1, 1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        if (random.nextInt(3) == 0) {
            world.setBlock(pos.offset(1, height - 4 - 1, -1), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        }
        world.setBlock(pos.offset(-2, height - 4, 0), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(2, height - 4, 0), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(0, height - 4, -2), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        world.setBlock(pos.offset(0, height - 4, 2), BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState(), 3);
        return true;
    }

    private void placeHorizontalLog(WorldGenLevel world, BlockPos pos, Direction.Axis axis) {
        world.setBlock(pos, BlockRegistry.SILVERWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), 3);
    }
}