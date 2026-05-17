package team.torka.thaumicrecords.event.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.torka.thaumicrecords.network.handler.PlayerCombineAspectHandler;
import team.torka.thaumicrecords.network.handler.PlayerEraseNoteHandler;
import team.torka.thaumicrecords.network.handler.PlayerWriteNoteHandler;
import team.torka.thaumicrecords.network.payload.PlayerCombineAspectPayload;
import team.torka.thaumicrecords.network.payload.PlayerEraseNotePayload;
import team.torka.thaumicrecords.network.payload.PlayerWriteNotePayload;

@EventBusSubscriber
public class RegisterPayloadHandlersEventListener {

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToServer(PlayerCombineAspectPayload.TYPE, PlayerCombineAspectPayload.STREAM_CODEC, PlayerCombineAspectHandler::handle);
        registrar.playToServer(PlayerWriteNotePayload.TYPE, PlayerWriteNotePayload.STREAM_CODEC, PlayerWriteNoteHandler::handle);
        registrar.playToServer(PlayerEraseNotePayload.TYPE, PlayerEraseNotePayload.STREAM_CODEC, PlayerEraseNoteHandler::handle);
    }
}