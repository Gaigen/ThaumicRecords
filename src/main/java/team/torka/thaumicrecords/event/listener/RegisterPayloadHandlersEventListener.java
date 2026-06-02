package team.torka.thaumicrecords.event.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.torka.thaumicrecords.network.handler.EssentiaSourcePayloadHandler;
import team.torka.thaumicrecords.network.handler.OreScanPayloadHandler;
import team.torka.thaumicrecords.network.handler.PlayerCombineAspectHandler;
import team.torka.thaumicrecords.network.handler.PlayerEraseNoteHandler;
import team.torka.thaumicrecords.network.handler.PlayerWriteNoteHandler;
import team.torka.thaumicrecords.network.handler.RunicShieldPayloadHandler;
import team.torka.thaumicrecords.network.handler.ShieldEffectPayloadHandler;
import team.torka.thaumicrecords.network.payload.EssentiaSourcePayload;
import team.torka.thaumicrecords.network.payload.OreScanPayload;
import team.torka.thaumicrecords.network.payload.PlayerCombineAspectPayload;
import team.torka.thaumicrecords.network.payload.PlayerEraseNotePayload;
import team.torka.thaumicrecords.network.payload.PlayerWriteNotePayload;
import team.torka.thaumicrecords.network.payload.RunicShieldPayload;
import team.torka.thaumicrecords.network.payload.ShieldEffectPayload;

@EventBusSubscriber
public class RegisterPayloadHandlersEventListener {

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToServer(PlayerCombineAspectPayload.TYPE, PlayerCombineAspectPayload.STREAM_CODEC, PlayerCombineAspectHandler::handle);
        registrar.playToServer(PlayerWriteNotePayload.TYPE, PlayerWriteNotePayload.STREAM_CODEC, PlayerWriteNoteHandler::handle);
        registrar.playToServer(PlayerEraseNotePayload.TYPE, PlayerEraseNotePayload.STREAM_CODEC, PlayerEraseNoteHandler::handle);
        registrar.playToClient(OreScanPayload.TYPE, OreScanPayload.STREAM_CODEC, OreScanPayloadHandler::handle);
        registrar.playToClient(RunicShieldPayload.TYPE, RunicShieldPayload.STREAM_CODEC, RunicShieldPayloadHandler::handle);
        registrar.playToClient(ShieldEffectPayload.TYPE, ShieldEffectPayload.STREAM_CODEC, ShieldEffectPayloadHandler::handle);
        registrar.playToClient(EssentiaSourcePayload.TYPE, EssentiaSourcePayload.STREAM_CODEC, EssentiaSourcePayloadHandler::handle);
    }
}
