package team.torka.thaumicrecords.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ScribingTool {
    boolean canScribe(ItemStack itemStack, Player player);

    void consumeDurability(ItemStack itemStack);
}
