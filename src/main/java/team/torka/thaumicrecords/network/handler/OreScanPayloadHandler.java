package team.torka.thaumicrecords.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.client.event.listener.OreScanHandler;
import team.torka.thaumicrecords.network.payload.OreScanPayload;

public class OreScanPayloadHandler {

    public static void handle(OreScanPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            OreScanHandler.startScan(context.player(), payload.center(), payload.range());
        });
    }
}
