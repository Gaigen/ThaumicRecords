package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

public class JarBlockEntity extends BlockEntity implements IEssentiaContainerItem {

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.JAR.get(), pos, state);
    }

    @Override
    public AspectList getAspects(ItemStack paramItemStack) {
        return paramItemStack.getOrDefault(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(AspectList.empty())).getAspects();
    }

    @Override
    public void setAspects(ItemStack paramItemStack, AspectList paramAspectList) {
        paramItemStack.set(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(paramAspectList));
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isLiquid() {
        return true;
    }

    @Override
    public int poursBy() {
        return 64;
    }

    @Override
    public int capacity() {
        return 64;
    }
}
