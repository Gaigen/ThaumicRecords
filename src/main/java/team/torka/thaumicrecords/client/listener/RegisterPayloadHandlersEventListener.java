package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.torka.thaumicrecords.network.payload.SyncAspectDiscoveryPayload;
import team.torka.thaumicrecords.network.payload.SyncResearchPointPayload;
import team.torka.thaumicrecords.registry.AttachmentRegistry;


@EventBusSubscriber(Dist.CLIENT)
public class RegisterPayloadHandlersEventListener {

    @SubscribeEvent
    public static void onEvent(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(SyncAspectDiscoveryPayload.TYPE, SyncAspectDiscoveryPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            context.player().setData(AttachmentRegistry.ASPECT_DISCOVERY, payload.data());
        }));
        registrar.playToClient(SyncResearchPointPayload.TYPE, SyncResearchPointPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            context.player().setData(AttachmentRegistry.RESEARCH_POINT, payload.data());
        }));
    }
}
