package team.torka.thaumicrecords.api.aspect;

import net.minecraft.world.item.ItemStack;

public interface IEssentiaContainerItem extends IEssentiaContainer {
    AspectList getAspects(ItemStack paramItemStack);

    void setAspects(ItemStack paramItemStack, AspectList paramAspectList);
}