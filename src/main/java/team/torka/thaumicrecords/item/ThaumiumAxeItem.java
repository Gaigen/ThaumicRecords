package team.torka.thaumicrecords.item;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ThaumiumAxeItem extends AxeItem {
    public ThaumiumAxeItem() {
        super(TierRegistry.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(AxeItem.createAttributes(TierRegistry.THAUMIUM, 5.0F, -3.0F)));
    }
}
