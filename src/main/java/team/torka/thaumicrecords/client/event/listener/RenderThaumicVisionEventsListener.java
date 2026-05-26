package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.client.event.RenderThaumicVisionEvent;

import java.util.Objects;

@EventBusSubscriber(Dist.CLIENT)
public class RenderThaumicVisionEventsListener {
    @SubscribeEvent
    public static void onNodeEvent(RenderThaumicVisionEvent.Node event) {
        Player player = event.getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (player.getMainHandItem().is(ModTags.SHOW_NODE_WHEN_HELD)) {
            event.setVisible(true);
        }
        if (player.getOffhandItem().is(ModTags.SHOW_NODE_WHEN_HELD)) {
            event.setVisible(true);
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.SHOW_NODE_WHEN_EQUIPPED)) {
            event.setVisible(true);
        }
    }

    @SubscribeEvent
    public static void onNodeEvent(RenderThaumicVisionEvent.Aspect event) {
        Player player = event.getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.SHOW_ASPECT_WHEN_EQUIPPED)) {
            event.setVisible(true);
        }
    }
}
