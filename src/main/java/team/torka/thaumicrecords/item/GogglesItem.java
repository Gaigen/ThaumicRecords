package team.torka.thaumicrecords.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.ArmorMaterialRegistry;

public class GogglesItem extends ArmorItem {

    public GogglesItem() {
        super(ArmorMaterialRegistry.GOGGLES, Type.HELMET, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(350));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

}
