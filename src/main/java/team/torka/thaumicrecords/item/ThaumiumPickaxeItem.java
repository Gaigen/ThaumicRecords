package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ThaumiumPickaxeItem extends PickaxeItem {
    public ThaumiumPickaxeItem() {
        super(TierRegistry.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(PickaxeItem.createAttributes(TierRegistry.THAUMIUM, 1.0F, -2.8F)));
    }
}
