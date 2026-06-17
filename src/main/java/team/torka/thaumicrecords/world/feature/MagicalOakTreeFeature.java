package team.torka.thaumicrecords.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Magical oak tree - ported from TC4 WorldGenBigMagicTree.
 * Large fancy oak with canopy, similar to GreatwoodTree but using OAK blocks.
 * Validates soil (dirt tag) before placement to avoid spawning on mushrooms/trees.
 */
public class MagicalOakTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final byte[] OTHER_COORD_PAIRS = {2, 0, 0, 1, 2, 1};

    private final double heightAttenuation = 0.6618;
    private final double branchSlope = 0.381;
    private final double leafDensity = 1.0;
    private final int heightLimitLimit = 12;
    private final int leafDistanceLimit = 3;

    public MagicalOakTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int heightLimit = heightLimitLimit + random.nextInt(heightLimitLimit);

        // TC4 validTreeLocation check: soil must support saplings
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }

        // Check vertical clearance (same as TC4 checkBlockLine for trunk)
        for (int y = 0; y < heightLimit; y++) {
            BlockPos checkPos = origin.above(y);
            if (!level.isEmptyBlock(checkPos) && !level.getBlockState(checkPos).canBeReplaced()) {
                return false;
            }
        }

        generateTreeInstance(level, origin, random, heightLimit);
        return true;
    }

    private void generateTreeInstance(WorldGenLevel level, BlockPos basePos, RandomSource rand, int heightLimit) {
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
                    double radius = size * (rand.nextFloat() + 0.328);
                    double angle = rand.nextFloat() * 2.0 * Math.PI;
                    int x = Mth.floor(radius * Math.sin(angle) + basePos.getX() + 0.5);
                    int z = Mth.floor(radius * Math.cos(angle) + basePos.getZ() + 0.5);
                    BlockPos nodePos = new BlockPos(x, basePos.getY() + relativeBaseY, z);

                    if (checkBlockLine(level, nodePos, nodePos.above(leafDistanceLimit)) == -1) {
                        double dist = Math.sqrt(Math.pow(basePos.getX() - x, 2) + Math.pow(basePos.getZ() - z, 2));
                        int branchY = (int) (nodePos.getY() - dist * branchSlope);
                        BlockPos branchStart;
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

        // Place leaf nodes and branches
        for (LeafNode node : leafNodes) {
            generateLeafNode(level, node.pos);
            if (node.pos.getY() - basePos.getY() >= heightLimit * 0.2) {
                placeBlockLine(level, basePos.atY(node.branchStartY), node.pos, logBlock());
            }
        }

        // Place trunk (height is relative to base, so add basePos.getY())
        placeBlockLine(level, basePos, basePos.atY(basePos.getY() + height), logBlock());
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

    private BlockState logBlock() {
        return Blocks.OAK_LOG.defaultBlockState();
    }

    private BlockState leavesBlock() {
        return Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
    }

    private record LeafNode(BlockPos pos, int branchStartY) {
    }
}
