package team.torka.thaumicrecords.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.ArmorMaterialRegistry;

public class GogglesItem extends ArmorItem {

    public GogglesItem() {
        super(ArmorMaterialRegistry.SPECIAL, Type.HELMET, new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    }
}
