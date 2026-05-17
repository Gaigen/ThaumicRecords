package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.AttachmentType;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class ArcanePedestalBlockEntity extends BlockEntity {
    public ArcanePedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ARCANE_PEDESTAL.get(), pos, blockState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public <T> boolean hasData(Supplier<AttachmentType<T>> type) {
        return super.hasData(type);
    }
}
