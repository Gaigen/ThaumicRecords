package team.torka.thaumicrecords.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.item.ScribingTool;

public class ScribingToolsItem extends Item implements ScribingTool {

    public ScribingToolsItem() {
        super(new Properties().stacksTo(1).durability(100));
    }

    @Override
    public boolean canScribe(ItemStack itemStack, Player player) {
        int damageValue = itemStack.getDamageValue();
        int maxDamage = itemStack.getMaxDamage();
        return damageValue < maxDamage;
    }

    @Override
    public void consumeDurability(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ScribingToolsItem) {
            int damageValue = itemStack.getDamageValue();
            itemStack.setDamageValue(damageValue + 1);
        }
    }
}
