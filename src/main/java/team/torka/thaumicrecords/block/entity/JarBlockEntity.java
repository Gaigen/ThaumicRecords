package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

public class JarBlockEntity extends BlockEntity {

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.JAR.get(), pos, state);
    }

    public static void onTick(
            Level level,
            BlockPos pos,
            BlockState state,
            JarBlockEntity blockEntity
    ) {

        if (level.isClientSide) {
            return;
        }

        // server tick logic here
    }

    public ItemStack getItem() {
        return null;
    }
}
