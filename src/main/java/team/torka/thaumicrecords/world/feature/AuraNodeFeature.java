package team.torka.thaumicrecords.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

public class AuraNodeFeature extends Feature<NoneFeatureConfiguration> {

    public AuraNodeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        // 生成概率 TODO 配置
        if (random.nextFloat() > 0.027F) {
            return false;
        }
        int height = random.nextInt(3);
        int minHeight = level.getMinBuildHeight();
        int maxHeight = level.getMaxBuildHeight();
        int totalHeightRange = maxHeight - minHeight;
        int randomY = minHeight + random.nextInt(totalHeightRange);

        BlockPos startScanPos = new BlockPos(pos.getX(), randomY, pos.getZ());
        // 位置
        Optional<BlockPos> validPosOpt = findExactNodePos(level, startScanPos, height);
        if (validPosOpt.isEmpty()) {
            return false;
        }

        ThaumicRecords.LOGGER.debug("created node at {}", validPosOpt.get());
        level.setBlock(validPosOpt.get(), BlockRegistry.AURA_NODE.get().defaultBlockState(), 2);
        return true;
    }

    private Optional<BlockPos> findExactNodePos(WorldGenLevel level, BlockPos startPos, int requiredAirHeight) {
        int minHeight = level.getMinBuildHeight();
        int maxHeight = level.getMaxBuildHeight();
        BlockPos.MutableBlockPos mutablePos = startPos.mutable();
        while (mutablePos.getY() > minHeight) {
            BlockPos belowPos = mutablePos.below();
            BlockState belowState = level.getBlockState(belowPos);

            boolean isAir = belowState.isAir();
            boolean isFluidFloor = !level.getFluidState(belowPos).isEmpty();
            if (!isAir || isFluidFloor) {
                boolean isAllAir = true;
                for (int i = 0; i <= requiredAirHeight; i++) {
                    if (mutablePos.getY() + i >= maxHeight || !level.isEmptyBlock(mutablePos.above(i))) {
                        isAllAir = false;
                        break;
                    }
                }
                if (isAllAir) {
                    return Optional.of(mutablePos.above(requiredAirHeight).immutable());
                }
            }
            mutablePos.move(Direction.DOWN);
        }
        return Optional.empty();
    }

}
