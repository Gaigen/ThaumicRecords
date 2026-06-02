package team.torka.thaumicrecords.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.client.InfusionSourceFXManager;
import team.torka.thaumicrecords.network.payload.InfusionSourcePayload;

/**
 * Handles InfusionSourcePayload on client — adds effect to InfusionSourceFXManager.
 * Actual particle spawning happens every tick in InfusionSourceTickListener.
 * Ported from TC4's PacketFXInfusionSource → TileInfusionMatrix.sourceFX.
 */
public class InfusionSourcePayloadHandler {

    public static void handle(InfusionSourcePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            InfusionSourceFXManager.addEffect(payload.matrixPos(), payload.sourcePos(), payload.item());
        });
    }
}
