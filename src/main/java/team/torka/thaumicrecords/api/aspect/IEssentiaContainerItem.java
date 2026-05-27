package team.torka.thaumicrecords.api.aspect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IEssentiaContainerItem extends IEssentiaContainer {
    AspectList getAspects(ItemStack paramItemStack);

    // to add aspects to container (container must hold logic by itself, so bool is indicator of success)
    // true->aspects was consumed and added, false, aspects wasnt consumed and was not added
    boolean addAspect(Player player, ItemStack stack, ResourceLocation aspect, int amount);

    void setAspects(ItemStack paramItemStack, AspectList paramAspectList);

    @Nullable
    Aspect getStoredAspect(ItemStack paramItemStack);

    @Nullable
    ResourceLocation getStoredAspectResource(ItemStack paramItemStack);

    int storedAmount(ItemStack paramItemStack);
}