package team.torka.thaumicrecords.api.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.IAspectSource;
import team.torka.thaumicrecords.network.payload.EssentiaSourcePayload;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static utility for scanning nearby IAspectSource blocks and draining essentia.
 * Used by InfusionMatrix and other blocks that need to pull essentia from jars.
 * <p>
 * Based on TC4's EssentiaHandler — scans a cube around the requesting block,
 * caches sources for 5 seconds to avoid rescanning every tick.
 */
public class EssentiaHandler {

    private static final long CACHE_DURATION_MS = 5000;

    // Cache: source position → list of nearby IAspectSource positions
    private static final Map<BlockPos, List<BlockPos>> sourcesCache = new HashMap<>();
    // Cache: source position → expiry timestamp
    private static final Map<BlockPos, Long> cacheExpiry = new HashMap<>();

    /**
     * Drains 1 essentia of the given aspect from a nearby IAspectSource.
     * Scans a cube of (2*range+1)^3 blocks around the requesting position.
     *
     * @param level  the world
     * @param pos    position of the requesting block (e.g. infusion matrix)
     * @param aspect the aspect to drain
     * @param range  scan radius in blocks (e.g. 12 for infusion)
     * @return true if essentia was successfully drained
     */
    public static boolean drainEssentia(Level level, BlockPos pos, Aspect aspect, int range) {
        List<BlockPos> sources = getOrCreateSources(level, pos, range);

        for (BlockPos sourcePos : sources) {
            BlockEntity be = level.getBlockEntity(sourcePos);
            if (be instanceof IAspectSource source) {
                if (source.storedAmount() > 0) {
                    Aspect storedAspect = source.getStoredAspect();
                    if (aspect.equals(storedAspect)) {
                        // Get the ResourceLocation for this aspect
                        ResourceLocation aspectId = AspectRegistry.ASPECT_REGISTRY.getKey(aspect);
                        if (aspectId != null) {
                            // Drain 1 essentia
                            source.wasPoured(aspectId, 1);

                            // Send visual effect to all nearby players
                            sendEssentiaSourceFX(level, pos, sourcePos, aspect);

                            return true;
                        }
                    }
                }
            }
        }

        // No source found with this aspect — clear cache and retry later
        sourcesCache.remove(pos);
        cacheExpiry.put(pos, System.currentTimeMillis() + CACHE_DURATION_MS);
        return false;
    }

    /**
     * Checks if a nearby IAspectSource contains the given aspect (without draining).
     */
    public static boolean findEssentia(Level level, BlockPos pos, Aspect aspect, int range) {
        List<BlockPos> sources = getOrCreateSources(level, pos, range);

        for (BlockPos sourcePos : sources) {
            BlockEntity be = level.getBlockEntity(sourcePos);
            if (be instanceof IAspectSource source) {
                if (source.storedAmount() > 0) {
                    Aspect storedAspect = source.getStoredAspect();
                    if (aspect.equals(storedAspect)) {
                        return true;
                    }
                }
            }
        }

        sourcesCache.remove(pos);
        cacheExpiry.put(pos, System.currentTimeMillis() + CACHE_DURATION_MS);
        return false;
    }

    /**
     * Gets the position of a nearby IAspectSource that contains the given aspect.
     * Used for rendering essentia stream particles.
     */
    public static BlockPos findSourcePosition(Level level, BlockPos pos, Aspect aspect, int range) {
        List<BlockPos> sources = getOrCreateSources(level, pos, range);

        for (BlockPos sourcePos : sources) {
            BlockEntity be = level.getBlockEntity(sourcePos);
            if (be instanceof IAspectSource source) {
                if (source.storedAmount() > 0) {
                    Aspect storedAspect = source.getStoredAspect();
                    if (aspect.equals(storedAspect)) {
                        return sourcePos;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Sends essentia source visual effect packet to all nearby players.
     */
    private static void sendEssentiaSourceFX(Level level, BlockPos matrixPos, BlockPos jarPos, Aspect aspect) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int color = aspect.getARGBColor() & 0xFFFFFF; // Remove alpha channel
        EssentiaSourcePayload payload = new EssentiaSourcePayload(matrixPos, jarPos, color);

        // Send to all players within 32 blocks
        for (ServerPlayer player : serverLevel.players()) {
            if (player.blockPosition().closerThan(matrixPos, 32)) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    /**
     * Gets or creates the cached list of nearby IAspectSource positions.
     */
    private static List<BlockPos> getOrCreateSources(Level level, BlockPos pos, int range) {
        // Check cache
        if (cacheExpiry.containsKey(pos)) {
            long expiry = cacheExpiry.get(pos);
            if (System.currentTimeMillis() < expiry) {
                List<BlockPos> cached = sourcesCache.get(pos);
                if (cached != null) {
                    return cached;
                }
            }
        }

        // Scan for sources
        List<BlockPos> sources = new ArrayList<>();
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                for (int dy = -range; dy <= range; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof IAspectSource) {
                        sources.add(checkPos);
                    }
                }
            }
        }

        sourcesCache.put(pos, sources);
        cacheExpiry.put(pos, System.currentTimeMillis() + CACHE_DURATION_MS);
        return sources;
    }

    /**
     * Clears the cache for a specific position (e.g. when a jar is broken).
     */
    public static void refreshSources(BlockPos pos) {
        sourcesCache.remove(pos);
        cacheExpiry.remove(pos);
    }

    /**
     * Clears all caches (e.g. on world unload).
     */
    public static void clearAll() {
        sourcesCache.clear();
        cacheExpiry.clear();
    }
}
