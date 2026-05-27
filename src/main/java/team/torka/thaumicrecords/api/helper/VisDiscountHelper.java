package team.torka.thaumicrecords.api.helper;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.IVisDiscountGear;
import team.torka.thaumicrecords.api.aspect.Aspect;

public class VisDiscountHelper {

    /**
     * Sums up the total vis discount from all equipped armor pieces.
     * Called when casting vis to reduce the cost.
     */
    public static int getTotalVisDiscount(Player player, Aspect aspect) {
        int total = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.getItem() instanceof IVisDiscountGear discountGear) {
                    total += discountGear.getVisDiscount(stack, player, aspect);
                }
            }
        }
        return total;
    }
}