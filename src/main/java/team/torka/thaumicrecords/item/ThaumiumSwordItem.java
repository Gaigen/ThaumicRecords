package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ThaumiumSwordItem extends SwordItem {
    public ThaumiumSwordItem() {
        super(TierRegistry.THAUMIUM, new Item.Properties().rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(TierRegistry.THAUMIUM, 3, -2.4F)));
    }
}
