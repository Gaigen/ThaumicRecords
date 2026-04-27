package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

public class ResearchTableBlockEntity extends BlockEntity {

    public ResearchTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.RESEARCH_TABLE.get(), pos, blockState);
    }
}
