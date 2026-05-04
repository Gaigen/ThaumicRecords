package team.torka.thaumicrecords.event.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.torka.thaumicrecords.network.handler.PlayerCombineAspectHandler;
import team.torka.thaumicrecords.network.payload.PlayerCombineAspectPayload;

@EventBusSubscriber
public class RegisterPayloadHandlersEventListener {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToServer(PlayerCombineAspectPayload.TYPE, PlayerCombineAspectPayload.CODEC, PlayerCombineAspectHandler::handle);
    }
}