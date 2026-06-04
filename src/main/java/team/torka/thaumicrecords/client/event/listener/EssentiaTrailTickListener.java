package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;
import team.torka.thaumicrecords.client.particle.EssentiaTrailParticle;
import team.torka.thaumicrecords.network.handler.EssentiaSourcePayloadHandler;
import team.torka.thaumicrecords.network.handler.EssentiaSourcePayloadHandler.EssentiaSourceFX;

import java.util.Iterator;
import java.util.List;

/**
 * Client-only tick listener that spawns essentia trail particles.
 * Reads pending effects from EssentiaSourcePayloadHandler and renders them.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class EssentiaTrailTickListener {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        ClientLevel level = mc.level;
        int tickCount = mc.player != null ? mc.player.tickCount : 0;

        List<EssentiaSourceFX> effects = EssentiaSourcePayloadHandler.getActiveEffects();
        Iterator<EssentiaSourceFX> iterator = effects.iterator();

        while (iterator.hasNext()) {
            EssentiaSourceFX fx = iterator.next();

            if (fx.ticks <= 0) {
                iterator.remove();
                continue;
            }

            // Spawn one particle per tick (matches TC4: scale shrinks in last 5 ticks)
            if (fx.ticks > 5) {
                spawnParticle(mc, level, fx, tickCount, 1.0F);
            } else {
                float scale = (fx.ticks * fx.ticks) / 25.0F;
                spawnParticle(mc, level, fx, tickCount - 5 - fx.ticks, scale);
            }

            fx.ticks--;
        }
    }

    private static void spawnParticle(Minecraft mc, ClientLevel level, EssentiaSourceFX fx, int count, float scale) {
        // Spawn from jar neck (top of block), not center — matches TC4
        Vec3 start = new Vec3(fx.jarPos.getX() + 0.5, fx.jarPos.getY() + 1.0, fx.jarPos.getZ() + 0.5);

        // mod = -1 for InfusionMatrix (matches TC4: particles fly to altar, not matrix center)
        int mod = 0;
        BlockEntity be = level.getBlockEntity(fx.matrixPos);
        if (be instanceof InfusionMatrixBlockEntity) {
            mod = -1;
        }

        Vec3 target = new Vec3(fx.matrixPos.getX() + 0.5, fx.matrixPos.getY() + 0.5 + mod, fx.matrixPos.getZ() + 0.5);

        float r = ((fx.color >> 16) & 0xFF) / 255.0F;
        float g = ((fx.color >> 8) & 0xFF) / 255.0F;
        float b = (fx.color & 0xFF) / 255.0F;

        EssentiaTrailParticle particle = new EssentiaTrailParticle(level, start.x, start.y, start.z, target.x, target.y, target.z, count, r, g, b, scale);
        mc.particleEngine.add(particle);
    }
}
