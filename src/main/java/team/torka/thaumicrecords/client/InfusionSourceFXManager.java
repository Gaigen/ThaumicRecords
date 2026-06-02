package team.torka.thaumicrecords.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.client.particle.InfusionItemParticle;
import team.torka.thaumicrecords.client.particle.InfusionSourceParticle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Client-side manager for infusion source particle effects.
 * Stores active effects and spawns particles every tick.
 * Ported from TC4's TileInfusionMatrix.sourceFX + doEffects.
 */
public class InfusionSourceFXManager {

    private static final List<SourceFX> activeEffects = new ArrayList<>();

    /**
     * Add a new source effect. Called when InfusionSourcePayload is received.
     */
    public static void addEffect(BlockPos matrixPos, BlockPos sourcePos, ItemStack item) {
        activeEffects.add(new SourceFX(matrixPos, sourcePos, item, 60)); // 60 ticks = 3 seconds
    }

    /**
     * Called every client tick to spawn particles for active effects.
     */
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return;
        }

        Iterator<SourceFX> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            SourceFX fx = iterator.next();

            if (fx.ticks <= 0) {
                iterator.remove();
                continue;
            }

            // Check if source pedestal still exists
            if (level.getBlockEntity(fx.sourcePos) == null) {
                fx.ticks = 0;
                continue;
            }

            double targetX = fx.matrixPos.getX() + 0.5;
            double targetY = fx.matrixPos.getY() - 0.5;
            double targetZ = fx.matrixPos.getZ() + 0.5;

            // TC4: rand.nextInt(3)==0 → purple sparkle (33%), else → item texture (67%)
            if (level.random.nextInt(3) == 0) {
                // Purple sparkle (drawInfusionParticles3): r=0.4+rand*0.2, g=0.2, b=0.6+rand*0.3
                double x = fx.sourcePos.getX() + level.random.nextFloat();
                double y = fx.sourcePos.getY() + level.random.nextFloat() + 1.0F;
                double z = fx.sourcePos.getZ() + level.random.nextFloat();

                float r = 0.4F + level.random.nextFloat() * 0.2F;
                float g = 0.2F;
                float b = 0.6F + level.random.nextFloat() * 0.3F;

                InfusionSourceParticle particle = new InfusionSourceParticle(level, x, y, z, targetX, targetY, targetZ, r, g, b);
                mc.particleEngine.add(particle);
            } else if (!fx.item.isEmpty()) {
                // Item texture particles (drawInfusionParticles1)
                for (int a = 0; a < 2; a++) {
                    double x = fx.sourcePos.getX() + 0.4F + level.random.nextFloat() * 0.2F;
                    double y = fx.sourcePos.getY() + 1.23F + level.random.nextFloat() * 0.2F;
                    double z = fx.sourcePos.getZ() + 0.4F + level.random.nextFloat() * 0.2F;

                    int face = level.random.nextInt(6);
                    InfusionItemParticle particle = new InfusionItemParticle(level, x, y, z, targetX, targetY, targetZ, fx.item, face);
                    mc.particleEngine.add(particle);
                }
            }

            fx.ticks--;
        }
    }

    /**
     * Clear all effects (e.g., on world unload).
     */
    public static void clear() {
        activeEffects.clear();
    }

    private static class SourceFX {
        BlockPos matrixPos;
        BlockPos sourcePos;
        ItemStack item;
        int ticks;

        SourceFX(BlockPos matrixPos, BlockPos sourcePos, ItemStack item, int ticks) {
            this.matrixPos = matrixPos;
            this.sourcePos = sourcePos;
            this.item = item;
            this.ticks = ticks;
        }
    }
}
