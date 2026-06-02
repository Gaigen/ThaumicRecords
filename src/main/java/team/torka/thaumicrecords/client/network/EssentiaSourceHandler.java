package team.torka.thaumicrecords.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.client.particle.EssentiaTrailParticle;
import team.torka.thaumicrecords.network.payload.EssentiaSourcePayload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles EssentiaSourcePayload on the client — spawns physics-based particles
 * traveling from jar to matrix, like TC4's FXEssentiaTrail.
 * <p>
 * TC4 behavior: spawns one particle per tick over 15 ticks.
 * Each packet represents ONE essentia drain event with a 15-tick visual effect.
 */
public class EssentiaSourceHandler {

    // Active effects — each spawns particles over 15 ticks
    private static final List<EssentiaSourceFX> activeEffects = new ArrayList<>();

    public static void handle(EssentiaSourcePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockPos matrixPos = payload.matrixPos();
            BlockPos jarPos = payload.jarPos();
            int color = payload.color();

            // Add new effect (each drain = one 15-tick effect)
            activeEffects.add(new EssentiaSourceFX(matrixPos, jarPos, color, 15));
        });
    }

    /**
     * Called every client tick to spawn particles for active effects.
     */
    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        ClientLevel level = mc.level;
        int tickCount = mc.player != null ? mc.player.tickCount : 0;

        Iterator<EssentiaSourceFX> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            EssentiaSourceFX fx = iterator.next();

            if (fx.ticks <= 0) {
                iterator.remove();
                continue;
            }

            // Spawn one particle per tick
            if (fx.ticks > 5) {
                spawnParticle(mc, level, fx, tickCount);
            } else {
                // Last 5 ticks: shrinking particles
                spawnParticle(mc, level, fx, tickCount - 5 - fx.ticks);
            }

            fx.ticks--;
        }
    }

    private static void spawnParticle(Minecraft mc, ClientLevel level, EssentiaSourceFX fx, int count) {
        Vec3 start = new Vec3(fx.jarPos.getX() + 0.5, fx.jarPos.getY() + 0.5, fx.jarPos.getZ() + 0.5);
        Vec3 target = new Vec3(fx.matrixPos.getX() + 0.5, fx.matrixPos.getY() + 0.5, fx.matrixPos.getZ() + 0.5);

        // Extract RGB from color
        float r = ((fx.color >> 16) & 0xFF) / 255.0F;
        float g = ((fx.color >> 8) & 0xFF) / 255.0F;
        float b = (fx.color & 0xFF) / 255.0F;

        EssentiaTrailParticle particle = new EssentiaTrailParticle(level, start.x, start.y, start.z, target.x, target.y, target.z, count, r, g, b, 1.0F);

        mc.particleEngine.add(particle);
    }

    /**
     * Active essentia source effect — tracks remaining ticks.
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
