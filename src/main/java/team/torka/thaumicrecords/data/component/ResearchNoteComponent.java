package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.helper.CubeCoordinateHelper;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public record ResearchNoteComponent(ResourceLocation research, int color, boolean complete, Map<String, HexEntry> hexes) {
    public static final Codec<ResearchNoteComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ResourceLocation.CODEC.fieldOf("research").forGetter(ResearchNoteComponent::research),
                    Codec.INT.fieldOf("color").forGetter(ResearchNoteComponent::color),
                    Codec.BOOL.fieldOf("complete").forGetter(ResearchNoteComponent::complete), Codec.unboundedMap(Codec.STRING, HexEntry.CODEC)
                            .fieldOf("hexes")
                            .forGetter(ResearchNoteComponent::hexes)).apply(instance, ResearchNoteComponent::new));
    public static final StreamCodec<ByteBuf, ResearchNoteComponent> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC,
            ResearchNoteComponent::research, ByteBufCodecs.INT, ResearchNoteComponent::color, ByteBufCodecs.BOOL, ResearchNoteComponent::complete,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, HexEntry.STREAM_CODEC), ResearchNoteComponent::hexes, ResearchNoteComponent::new);

    public Map<CubeCoordinateHelper.CubeHex, HexEntry> getDecodedHexes() {
        Map<CubeCoordinateHelper.CubeHex, HexEntry> decoded = new HashMap<>();
        hexes.forEach((key, entry) -> decoded.put(CubeCoordinateHelper.CubeHex.fromKey(key), entry));
        return decoded;
    }

    public record HexEntry(int type, @Nullable ResourceLocation aspect) {
        public static final int EMPTY = 0;
        public static final int ROOT = 1;
        public static final int FULL = 2;
        public static final Codec<HexEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("type").forGetter(HexEntry::type),
                        ResourceLocation.CODEC.optionalFieldOf("aspect").forGetter(entry -> Optional.ofNullable(entry.aspect)))
                .apply(instance, (t, a) -> new HexEntry(t, a.orElse(null))));
        public static final StreamCodec<ByteBuf, HexEntry> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, HexEntry::type,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), entry -> Optional.ofNullable(entry.aspect), (t, a) -> new HexEntry(t, a.orElse(null)));
    }

    public boolean canWriteTo(String coordinate) {
        if (this.complete) {
            return false;
        }
        HexEntry entry = hexes.get(coordinate);
        return Objects.nonNull(coordinate) && entry.type() == HexEntry.EMPTY;
    }

    public ResearchNoteComponent writeHex(String coordinate, HexEntry newEntry) {
        Map<String, HexEntry> newHexes = new HashMap<>(this.hexes);
        newHexes.put(coordinate, newEntry);
        return new ResearchNoteComponent(this.research, this.color, this.complete, Collections.unmodifiableMap(newHexes));
    }

    public ResearchNoteComponent finishedOrSelf() {
        if (isNoteFinished()) {
            Map<String, HexEntry> newHexes = new HashMap<>(this.hexes);
            List<CubeCoordinateHelper.CubeHex> disconnected = getDisconnectedFullHexes();
            for (CubeCoordinateHelper.CubeHex hex : disconnected) {
                newHexes.put(hex.toKey(), new HexEntry(HexEntry.EMPTY, null));
            }
            return new ResearchNoteComponent(this.research, this.color, true, Collections.unmodifiableMap(newHexes));
        }
        return this;
    }

    public boolean isNoteFinished() {
        Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> decoded = this.getDecodedHexes();
        List<CubeCoordinateHelper.CubeHex> roots = new ArrayList<>();
        decoded.forEach((pos, entry) -> {
            if (entry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                roots.add(pos);
            }
        });
        if (roots.isEmpty()) {
            return true;
        }
        if (roots.size() == 1) {
            return true;
        }
        Set<CubeCoordinateHelper.CubeHex> visited = new HashSet<>();
        Queue<CubeCoordinateHelper.CubeHex> queue = new LinkedList<>();
        CubeCoordinateHelper.CubeHex startNode = roots.get(0);
        queue.add(startNode);
        visited.add(startNode);
        while (!queue.isEmpty()) {
            CubeCoordinateHelper.CubeHex current = queue.poll();
            ResearchNoteComponent.HexEntry currentEntry = decoded.get(current);
            Aspect currentAspect = AspectRegistry.ASPECT_REGISTRY.get(currentEntry.aspect());
            for (int i = 0; i < 6; i++) {
                CubeCoordinateHelper.CubeHex neighbor = current.getNeighbor(i);
                if (decoded.containsKey(neighbor) && !visited.contains(neighbor)) {
                    ResearchNoteComponent.HexEntry neighborEntry = decoded.get(neighbor);
                    if (neighborEntry.type() != ResearchNoteComponent.HexEntry.EMPTY) {
                        Aspect neighborAspect = AspectRegistry.ASPECT_REGISTRY.get(neighborEntry.aspect());
                        if (Objects.nonNull(currentAspect) && Objects.nonNull(neighborAspect) && currentAspect.isRelatedTo(neighborAspect)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        for (CubeCoordinateHelper.CubeHex root : roots) {
            if (!visited.contains(root)) {
                return false;
            }
        }
        return true;
    }

    public List<CubeCoordinateHelper.CubeHex> getDisconnectedFullHexes() {
        Map<CubeCoordinateHelper.CubeHex, HexEntry> decodedHexes = this.getDecodedHexes();
        Set<CubeCoordinateHelper.CubeHex> connected = new HashSet<>();
        Queue<CubeCoordinateHelper.CubeHex> queue = new LinkedList<>();

        decodedHexes.forEach((pos, entry) -> {
            if (entry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                queue.add(pos);
                connected.add(pos);
            }
        });

        while (!queue.isEmpty()) {
            CubeCoordinateHelper.CubeHex current = queue.poll();
            for (int i = 0; i < 6; i++) {
                CubeCoordinateHelper.CubeHex neighbor = current.getNeighbor(i);
                if (decodedHexes.containsKey(neighbor)) {
                    ResearchNoteComponent.HexEntry neighborEntry = decodedHexes.get(neighbor);
                    if (neighborEntry.type() == ResearchNoteComponent.HexEntry.FULL && !connected.contains(neighbor)) {
                        Aspect currentAspect = AspectRegistry.ASPECT_REGISTRY.get(decodedHexes.get(current).aspect());
                        Aspect neighborAspect = AspectRegistry.ASPECT_REGISTRY.get(decodedHexes.get(neighbor).aspect());
                        if (Objects.nonNull(currentAspect) && Objects.nonNull(neighborAspect)) {
                            if (currentAspect.isRelatedTo(neighborAspect)) {
                                connected.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        List<CubeCoordinateHelper.CubeHex> disconnected = new ArrayList<>();
        decodedHexes.forEach((pos, entry) -> {
            if (entry.type() == ResearchNoteComponent.HexEntry.FULL && !connected.contains(pos)) {
                disconnected.add(pos);
            }
        });
        return disconnected;
    }

    public Set<HexLink> getAllLinks() {
        Map<CubeCoordinateHelper.CubeHex, HexEntry> decodedHexes = this.getDecodedHexes();
        Set<HexLink> links = new HashSet<>();
        List<CubeCoordinateHelper.CubeHex> disconnected = getDisconnectedFullHexes();
        for (Map.Entry<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> entry : decodedHexes.entrySet()) {
            CubeCoordinateHelper.CubeHex pos = entry.getKey();
            ResearchNoteComponent.HexEntry current = entry.getValue();
            if (current.type() == ResearchNoteComponent.HexEntry.EMPTY) {
                continue;
            }
            for (int i = 0; i < 6; i++) {
                CubeCoordinateHelper.CubeHex neighborPos = pos.getNeighbor(i);
                if (decodedHexes.containsKey(neighborPos)) {
                    ResearchNoteComponent.HexEntry neighborEntry = decodedHexes.get(neighborPos);
                    if (disconnected.contains(pos) || disconnected.contains(neighborPos)) {
                        continue;
                    }
                    if (neighborEntry.type() != ResearchNoteComponent.HexEntry.EMPTY) {
                        Aspect currentAspect = AspectRegistry.ASPECT_REGISTRY.get(current.aspect());
                        Aspect neighborAspect = AspectRegistry.ASPECT_REGISTRY.get(neighborEntry.aspect());
                        if (Objects.nonNull(currentAspect) && Objects.nonNull(neighborAspect)) {
                            if (currentAspect.isRelatedTo(neighborAspect)) {
                                links.add(new HexLink(pos, neighborPos));
                            }
                        }
                    }
                }
            }
        }
        return links;
    }

    public record HexLink(CubeCoordinateHelper.CubeHex a, CubeCoordinateHelper.CubeHex b) {
        public HexLink {
            if (a.hashCode() > b.hashCode()) {
                CubeCoordinateHelper.CubeHex temp = a;
                a = b;
                b = temp;
            }
        }
    }
}

