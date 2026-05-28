package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioItem extends Item implements ICurioItem {

    protected final String curioSlot;

    public CurioItem(String curioSlot, Properties properties) {
        super(properties);
        this.curioSlot = curioSlot;
    }

    public CurioItem(String curioSlot) {
        this(curioSlot, new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals(curioSlot);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
