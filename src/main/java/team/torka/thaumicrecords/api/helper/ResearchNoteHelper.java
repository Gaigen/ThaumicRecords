package team.torka.thaumicrecords.api.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResearchNoteHelper {

    public static ResearchNoteComponent generateNote(Research research, ResourceLocation researchKey, RandomSource random) {
        int radius = 1 + Math.min(3, research.gridSize);

        List<CubeCoordinateHelper.CubeHex> allHexes = generateHexes(radius);

        AspectList aspects = research.aspects;
        List<ResourceLocation> aspectKeys = new ArrayList<>(aspects.keySet());
        aspectKeys.removeIf(rl -> AspectRegistry.ASPECT_REGISTRY.get(rl) == null);

        List<CubeCoordinateHelper.CubeHex> outerRing = getRing(radius);
        List<CubeCoordinateHelper.CubeHex> rootPositions = distributeRingRandomly(outerRing, aspectKeys.size(), random);

        Map<String, ResearchNoteComponent.HexEntry> hexes = new HashMap<>();
        for (CubeCoordinateHelper.CubeHex hex : allHexes) {
            hexes.put(hex.toKey(), new ResearchNoteComponent.HexEntry(ResearchNoteComponent.HexEntry.EMPTY, null));
        }

        for (int i = 0; i < rootPositions.size(); i++) {
            ResourceLocation aspectRl = aspectKeys.get(i % aspectKeys.size());
            hexes.put(rootPositions.get(i).toKey(), new ResearchNoteComponent.HexEntry(ResearchNoteComponent.HexEntry.ROOT, aspectRl));
        }

        if (research.gridSize > 1) {
            int removeCount = research.gridSize * 2;
            List<CubeCoordinateHelper.CubeHex> emptyHexes = new ArrayList<>();
            for (CubeCoordinateHelper.CubeHex hex : allHexes) {
                ResearchNoteComponent.HexEntry entry = hexes.get(hex.toKey());
                if (entry.type() == ResearchNoteComponent.HexEntry.EMPTY) {
                    emptyHexes.add(hex);
                }
            }
            shuffleList(emptyHexes, random);

            int removed = 0;
            for (CubeCoordinateHelper.CubeHex hex : emptyHexes) {
                if (removed >= removeCount) {
                    break;
                }
                if (canRemoveHex(hex, hexes)) {
                    hexes.remove(hex.toKey());
                    removed++;
                }
            }
        }

        int color = 0x999999;
        Aspect primaryAspect = getPrimaryAspect(aspects);
        if (primaryAspect != null) {
            color = primaryAspect.getARGBColor();
        }

        return new ResearchNoteComponent(researchKey, color, false, Collections.unmodifiableMap(hexes));
    }


    private static List<CubeCoordinateHelper.CubeHex> generateHexes(int radius) {
        List<CubeCoordinateHelper.CubeHex> results = new ArrayList<>();
        results.add(new CubeCoordinateHelper.CubeHex(0, 0));

        for (int k = 0; k < radius; k++) {
            CubeCoordinateHelper.CubeHex h = new CubeCoordinateHelper.CubeHex(0, 0);
            for (int step = 0; step <= k; step++) {
                h = h.getNeighbor(4);
            }

            CubeCoordinateHelper.CubeHex hd = new CubeCoordinateHelper.CubeHex(h.x(), h.z());
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j <= k; j++) {
                    results.add(new CubeCoordinateHelper.CubeHex(hd.x(), hd.z()));
                    hd = hd.getNeighbor(i);
                }
            }
        }
        return results;
    }


    private static List<CubeCoordinateHelper.CubeHex> getRing(int radius) {
        CubeCoordinateHelper.CubeHex h = new CubeCoordinateHelper.CubeHex(0, 0);
        for (int k = 0; k < radius; k++) {
            h = h.getNeighbor(4);
        }

        List<CubeCoordinateHelper.CubeHex> ring = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < radius; j++) {
                ring.add(new CubeCoordinateHelper.CubeHex(h.x(), h.z()));
                h = h.getNeighbor(i);
            }
        }
        return ring;
    }


    private static List<CubeCoordinateHelper.CubeHex> distributeRingRandomly(List<CubeCoordinateHelper.CubeHex> ring, int entries, RandomSource random) {
        List<CubeCoordinateHelper.CubeHex> results = new ArrayList<>();
        if (ring.isEmpty() || entries <= 0) {
            return results;
        }

        float spacing = (float) ring.size() / (float) entries;
        random.nextInt(ring.size());
        float pos = 0.0F;

        for (int i = 0; i < entries; i++) {
            results.add(ring.get(Math.round(pos)));
            pos += spacing;
        }
        return results;
    }


    private static Aspect getPrimaryAspect(AspectList aspects) {
        Aspect result = null;
        int highest = 0;
        for (Map.Entry<ResourceLocation, Integer> entry : aspects.entrySet()) {
            if (entry.getValue() > highest) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
                if (aspect != null) {
                    result = aspect;
                    highest = entry.getValue();
                }
            }
        }
        return result;
    }

    private static <T> void shuffleList(List<T> list, RandomSource random) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    private static boolean canRemoveHex(CubeCoordinateHelper.CubeHex hex, Map<String, ResearchNoteComponent.HexEntry> hexes) {
        for (int i = 0; i < 6; i++) {
            CubeCoordinateHelper.CubeHex neighbor = hex.getNeighbor(i);
            ResearchNoteComponent.HexEntry neighborEntry = hexes.get(neighbor.toKey());
            if (neighborEntry != null && neighborEntry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                int neighborCount = 0;
                for (int j = 0; j < 6; j++) {
                    CubeCoordinateHelper.CubeHex nn = neighbor.getNeighbor(j);
                    if (hexes.containsKey(nn.toKey()) && !nn.equals(hex)) {
                        neighborCount++;
                    }
                }
                if (neighborCount < 2) {
                    return false;
                }
            }
        }
        return true;
    }
}
