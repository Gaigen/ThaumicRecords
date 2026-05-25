package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;

public class PhialItem extends Item implements IEssentiaContainerItem {
    public PhialItem(Properties properties) {
        super(properties);
    }

    @Override
    public AspectList getAspects(ItemStack paramItemStack) {
        return null;
    }

    @Override
    public void setAspects(ItemStack paramItemStack, AspectList paramAspectList) {

    }
}
