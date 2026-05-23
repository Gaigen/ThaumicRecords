package team.torka.thaumicrecords.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import team.torka.thaumicrecords.registry.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GreatwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final byte[] OTHER_COORD_PAIRS = {2, 0, 0, 1, 2, 1};

    private final double heightAttenuation = 0.618;
    private final double branchSlope = 0.38;
    private final double leafDensity = 0.9;
    private final int trunkSize = 2;
    private final int heightLimitLimit = 11;
    private final int leafDistanceLimit = 4;

    public GreatwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        boolean isFromSapling = context.level().getBlockState(context.origin()).is(BlockRegistry.GREATWOOD_SAPLING.get());

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int heightLimit = heightLimitLimit + random.nextInt(heightLimitLimit);

        if (!checkValidLocation(level, origin)) {
            return false;
        }

        generateTreeInstance(level, origin, random, heightLimit, 1.2);


        int layer2Height = (int) (heightLimit * heightAttenuation);
        generateTreeInstance(level, origin.above(layer2Height), random, heightLimit, 1.66);

        if (!isFromSapling && random.nextInt(8) == 0) {
            spawnSpiderLair(level, origin, random);
        }

        return true;
    }

    private boolean checkValidLocation(WorldGenLevel level, BlockPos pos) {
        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                if (!level.getBlockState(pos.offset(x, -1, z)).is(BlockTags.DIRT)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateTreeInstance(WorldGenLevel level, BlockPos basePos, RandomSource rand, int heightLimit, double scaleWidth) {
        int height = (int) (heightLimit * heightAttenuation);
        if (height >= heightLimit) {
            height = heightLimit - 1;
        }

        List<LeafNode> leafNodes = new ArrayList<>();
        int leafNodesCount = (int) (1.382 + Math.pow(leafDensity * heightLimit / 13.0, 2.0));
        if (leafNodesCount < 1) {
            leafNodesCount = 1;
        }

        int baseY = basePos.getY() + heightLimit - leafDistanceLimit;
        int trunkTopY = basePos.getY() + height;
        int relativeBaseY = baseY - basePos.getY();

        leafNodes.add(new LeafNode(basePos.atY(baseY), trunkTopY));

        while (relativeBaseY >= 0) {
            float size = layerSize(relativeBaseY, heightLimit);
            if (size >= 0.0F) {
                for (int i = 0; i < leafNodesCount; i++) {
                    double radius = scaleWidth * size * (rand.nextFloat() + 0.328);
                    double angle = rand.nextFloat() * 2.0 * Math.PI;
                    int x = Mth.floor(radius * Math.sin(angle) + basePos.getX() + 0.5);
                    int z = Mth.floor(radius * Math.cos(angle) + basePos.getZ() + 0.5);
                    BlockPos nodePos = new BlockPos(x, basePos.getY() + relativeBaseY, z);

                    if (checkBlockLine(level, nodePos, nodePos.above(leafDistanceLimit)) == -1) {
                        BlockPos branchStart = basePos.atY(trunkTopY);
                        double dist = Math.sqrt(Math.pow(basePos.getX() - x, 2) + Math.pow(basePos.getZ() - z, 2));
                        int branchY = (int) (nodePos.getY() - dist * branchSlope);
                        if (branchY > trunkTopY) {
                            branchStart = basePos.atY(trunkTopY);
                        } else {
                            branchStart = basePos.atY(branchY);
                        }

                        if (checkBlockLine(level, branchStart, nodePos) == -1) {
                            leafNodes.add(new LeafNode(nodePos, branchStart.getY()));
                        }
                    }
                }
            }
            relativeBaseY--;
        }

        for (LeafNode node : leafNodes) {
            generateLeafNode(level, node.pos);
            if (node.pos.getY() - basePos.getY() >= heightLimit * 0.2) {
                placeBlockLine(level, basePos.atY(node.branchStartY), node.pos, logBlock());
            }
        }

        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                placeBlockLine(level, basePos.offset(x, 0, z), basePos.offset(x, height, z), logBlock());
            }
        }
    }

    private float layerSize(int y, int heightLimit) {
        if (y < heightLimit * 0.3) {
            return -1.618F;
        }
        float halfHeight = heightLimit / 2.0F;
        float distFromHalf = halfHeight - y;
        float size = (distFromHalf == 0) ? halfHeight : (Math.abs(distFromHalf) >= halfHeight) ? 0.0F : (float) Math.sqrt(
                Math.pow(halfHeight, 2) - Math.pow(distFromHalf, 2));
        return size * 0.5F;
    }

    private void generateLeafNode(WorldGenLevel level, BlockPos pos) {
        for (int y = 0; y < leafDistanceLimit; y++) {
            float size = (y != 0 && y != leafDistanceLimit - 1) ? 3.0F : 2.0F;
            genTreeLayer(level, pos.above(y), size, (byte) 1, leavesBlock());
        }
    }

    private void genTreeLayer(WorldGenLevel level, BlockPos pos, float radius, byte axis, BlockState state) {
        int r = (int) (radius + 0.618);
        byte xIdx = OTHER_COORD_PAIRS[axis];
        byte zIdx = OTHER_COORD_PAIRS[axis + 3];
        int[] originArr = {pos.getX(), pos.getY(), pos.getZ()};
        int[] currentArr = {0, 0, 0};
        currentArr[axis] = originArr[axis];

        for (int i = -r; i <= r; ++i) {
            currentArr[xIdx] = originArr[xIdx] + i;
            for (int j = -r; j <= r; ++j) {
                double dist = Math.pow(Math.abs(i) + 0.5, 2) + Math.pow(Math.abs(j) + 0.5, 2);
                if (dist <= (radius * radius)) {
                    currentArr[zIdx] = originArr[zIdx] + j;
                    BlockPos target = new BlockPos(currentArr[0], currentArr[1], currentArr[2]);
                    if (level.isEmptyBlock(target) || level.getBlockState(target).is(BlockTags.LEAVES)) {
                        level.setBlock(target, state, 3);
                    }
                }
            }
        }
    }

    private void placeBlockLine(WorldGenLevel level, BlockPos start, BlockPos end, BlockState state) {
        int[] startArr = {start.getX(), start.getY(), start.getZ()};
        int[] endArr = {end.getX(), end.getY(), end.getZ()};
        int[] diff = {endArr[0] - startArr[0], endArr[1] - startArr[1], endArr[2] - startArr[2]};

        byte maxIdx = 0;
        for (byte i = 1; i < 3; i++) {
            if (Math.abs(diff[i]) > Math.abs(diff[maxIdx])) {
                maxIdx = i;
            }
        }

        if (diff[maxIdx] == 0) {
            return;
        }

        byte xIdx = OTHER_COORD_PAIRS[maxIdx];
        byte zIdx = OTHER_COORD_PAIRS[maxIdx + 3];
        int step = diff[maxIdx] > 0 ? 1 : -1;
        double xSlope = (double) diff[xIdx] / diff[maxIdx];
        double zSlope = (double) diff[zIdx] / diff[maxIdx];

        for (int i = 0; i != diff[maxIdx] + step; i += step) {
            int x = Mth.floor(startArr[maxIdx] + i + 0.5);
            int y = Mth.floor(startArr[xIdx] + i * xSlope + 0.5);
            int z = Mth.floor(startArr[zIdx] + i * zSlope + 0.5);

            int[] posArr = new int[3];
            posArr[maxIdx] = x;
            posArr[xIdx] = y;
            posArr[zIdx] = z;
            BlockPos target = new BlockPos(posArr[0], posArr[1], posArr[2]);

            BlockState finalState = state;
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                int dx = Math.abs(target.getX() - start.getX());
                int dz = Math.abs(target.getZ() - start.getZ());
                int maxDist = Math.max(dx, dz);
                if (maxDist > 0) {
                    if (dx == maxDist) {
                        finalState = state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
                    } else {
                        finalState = state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
                    }
                }
            }
            level.setBlock(target, finalState, 3);
        }
    }

    private int checkBlockLine(WorldGenLevel level, BlockPos start, BlockPos end) {
        int[] s = {start.getX(), start.getY(), start.getZ()};
        int[] e = {end.getX(), end.getY(), end.getZ()};
        int[] d = {e[0] - s[0], e[1] - s[1], e[2] - s[2]};
        byte m = 0;
        for (byte i = 1; i < 3; i++) {
            if (Math.abs(d[i]) > Math.abs(d[m])) {
                m = i;
            }
        }
        if (d[m] == 0) {
            return -1;
        }

        int step = d[m] > 0 ? 1 : -1;
        for (int i = 0; i != d[m] + step; i += step) {
            BlockPos p = start.offset((int) (i * (double) d[0] / d[m]), (int) (i * (double) d[1] / d[m]), (int) (i * (double) d[2] / d[m]));
            if (!level.isEmptyBlock(p) && !level.getBlockState(p).is(BlockTags.LEAVES)) {
                return Math.abs(i);
            }
        }
        return -1;
    }

    private void spawnSpiderLair(WorldGenLevel level, BlockPos pos, RandomSource rand) {
        level.setBlock(pos.below(1), Blocks.SPAWNER.defaultBlockState(), 3);
        if (level.getBlockEntity(pos.below(1)) instanceof SpawnerBlockEntity spawner) {
            spawner.setEntityId(EntityType.CAVE_SPIDER, rand);
        }
        BlockPos chestPos = pos.below(2);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof RandomizableContainerBlockEntity chest) {
            // TODO 战利品箱
            chest.setLootTable(BuiltInLootTables.SIMPLE_DUNGEON, rand.nextLong());
        }

        for (int i = 0; i < 50; i++) {
            BlockPos webPos = pos.offset(rand.nextInt(14) - 7, rand.nextInt(10), rand.nextInt(14) - 7);
            if (level.isEmptyBlock(webPos)) {
                for (Direction dir : Direction.values()) {
                    if (level.getBlockState(webPos.relative(dir)).is(leavesBlock().getBlock()) || level.getBlockState(webPos.relative(dir)).is(
                            logBlock().getBlock())) {
                        level.setBlock(webPos, Blocks.COBWEB.defaultBlockState(), 3);
                        break;
                    }
                }
            }
        }
    }

    private BlockState logBlock() {
        return BlockRegistry.GREATWOOD_LOG.get().defaultBlockState();
    }

    private BlockState leavesBlock() {
        return BlockRegistry.GREATWOOD_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
    }

    private record LeafNode(BlockPos pos, int branchStartY) {
    }
}
