package team.torka.thaumicrecords.item;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ThaumiumHoeItem extends HoeItem {
    public ThaumiumHoeItem() {
        super(TierRegistry.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(HoeItem.createAttributes(TierRegistry.THAUMIUM, -3.0F, 0.0F)));
    }
}
