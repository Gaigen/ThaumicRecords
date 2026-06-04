package team.torka.thaumicrecords.network.handler;

import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.network.payload.EssentiaSourcePayload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles EssentiaSourcePayload — stores pending effects on client.
 * Particle spawning is done by EssentiaTrailTickListener (client-only)
 * which calls clientTick() via a separate client-safe accessor.
 */
public class EssentiaSourcePayloadHandler {

    // Active effects — each spawns particles over 15 ticks
    private static final List<EssentiaSourceFX> activeEffects = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public static void handle(EssentiaSourcePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            activeEffects.add(new EssentiaSourceFX(payload.matrixPos(), payload.jarPos(), payload.color(), 15));
        });
    }

    /**
     * Get active effects (called from client-only code).
     */
    public static List<EssentiaSourceFX> getActiveEffects() {
        return activeEffects;
    }

    /**
     * Active essentia source effect — tracks remaining ticks.
     * Pure data class, no client dependencies.
     */
    public static class EssentiaSourceFX {
        public BlockPos matrixPos;
        public BlockPos jarPos;
        public int color;
        public int ticks;

        public EssentiaSourceFX(BlockPos matrixPos, BlockPos jarPos, int color, int ticks) {
            this.matrixPos = matrixPos;
            this.jarPos = jarPos;
            this.color = color;
            this.ticks = ticks;
        }
    }
}
