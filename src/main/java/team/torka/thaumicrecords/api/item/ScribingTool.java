package team.torka.thaumicrecords.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ScribingTool {
    default boolean canScribe(ItemStack itemStack, Player player) {
        int damageValue = itemStack.getDamageValue();
        int maxDamage = itemStack.getMaxDamage();
        return damageValue < maxDamage;
    }

    default void consumeDurability(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ScribingTool) {
            int damageValue = itemStack.getDamageValue();
            itemStack.setDamageValue(damageValue + 1);
        }
    }
}
