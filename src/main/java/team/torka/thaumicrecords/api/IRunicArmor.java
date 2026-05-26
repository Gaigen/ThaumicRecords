package team.torka.thaumicrecords.api;

import net.minecraft.world.item.ItemStack;

/**
 * Implemented by armor pieces that provide runic shielding.
 */
public interface IRunicArmor {

    /**
     * @return maximum runic shielding charge for this item
     */
    int getRunicCharge(ItemStack itemstack);
}
