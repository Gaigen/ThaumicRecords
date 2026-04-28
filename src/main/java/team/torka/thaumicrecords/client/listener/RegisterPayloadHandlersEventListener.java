package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.torka.thaumicrecords.network.packet.SyncAspectDiscoveryPacket;
import team.torka.thaumicrecords.registry.AttachmentRegistry;


@EventBusSubscriber(Dist.CLIENT)
public class RegisterPayloadHandlersEventListener {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(SyncAspectDiscoveryPacket.TYPE, SyncAspectDiscoveryPacket.STREAM_CODEC, ((payload, context) -> context.enqueueWork(() -> {
            context.player().setData(AttachmentRegistry.ASPECT_DISCOVERY, payload.data());
        })));
    }
}
