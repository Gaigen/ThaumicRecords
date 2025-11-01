package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

public class Wand extends Item {

    public Wand(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    public WandRod getRod(ItemStack stack) {
        return WandRodRegistry.WAND_ROD_WOOD.get();
    }

    public WandCap getCap(ItemStack stack) {
        return WandCapRegistry.WAND_CAP_IRON.get();
    }


}
