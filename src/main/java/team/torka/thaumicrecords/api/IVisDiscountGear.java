package team.torka.thaumicrecords.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.aspect.Aspect;

public interface IVisDiscountGear {
    int getVisDiscount(ItemStack stack, Player player, Aspect aspect);
}