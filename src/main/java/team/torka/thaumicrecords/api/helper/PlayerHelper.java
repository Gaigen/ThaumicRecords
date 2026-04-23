package team.torka.thaumicrecords.api.helper;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import team.torka.thaumicrecords.api.ModTags;

public class PlayerHelper {

    public static boolean shouldShowNode(Player player) {
        if (player == null) {
            return false;
        }
        if (player.getMainHandItem().is(ModTags.SHOW_NODE_WHEN_HELD)) {
            return true;
        }
        if (player.getOffhandItem().is(ModTags.SHOW_NODE_WHEN_HELD)) {
            return true;
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.SHOW_NODE_WHEN_EQUIPPED)) {
            return true;
        }

        return false;
    }
}
