package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ThaumiumShovelItem extends ShovelItem {
    public ThaumiumShovelItem() {
        super(TierRegistry.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(ShovelItem.createAttributes(TierRegistry.THAUMIUM, 1.5F, -3.0F)));
    }
}
