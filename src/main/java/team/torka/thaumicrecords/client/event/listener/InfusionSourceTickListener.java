package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import team.torka.thaumicrecords.client.InfusionSourceFXManager;

/**
 * Client tick listener that spawns infusion source particles every tick.
 * Ported from TC4's TileInfusionMatrix.doEffects client-side loop.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class InfusionSourceTickListener {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        InfusionSourceFXManager.tick();
    }
}
