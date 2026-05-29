package team.torka.thaumicrecords.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.client.event.EventHandlerRunic;
import team.torka.thaumicrecords.network.payload.RunicShieldPayload;

public class RunicShieldPayloadHandler {

    public static void handle(RunicShieldPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                EventHandlerRunic.setClientShieldData(context.player().getUUID(), payload.currentCharge(), payload.maxCharge());
            }
        });
    }
}
