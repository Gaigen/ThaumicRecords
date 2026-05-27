package team.torka.thaumicrecords.api.aspect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IEssentiaContainerItem extends IEssentiaContainer {
    AspectList getAspects(ItemStack paramItemStack);

    void setAspects(ItemStack paramItemStack, AspectList paramAspectList);

    @Nullable
    Aspect getStoredAspect(ItemStack paramItemStack);

    @Nullable
    ResourceLocation getStoredAspectResource(ItemStack paramItemStack);

    int storedAmount(ItemStack paramItemStack);
}