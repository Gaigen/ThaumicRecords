package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.helper.CubeCoordinateHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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


    @Nullable
    public HexEntry getHex(CubeCoordinateHelper.CubeHex pos) {
        return hexes.get(pos.toKey());
    }

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

        public boolean isRoot() {
            return type == ROOT;
        }
    }

    public boolean canWriteTo(String coordinate) {
        HexEntry entry = hexes.get(coordinate);
        return Objects.nonNull(coordinate) && entry.type() == HexEntry.EMPTY;
    }

    public ResearchNoteComponent writeHex(String coordinate, HexEntry newEntry) {
        Map<String, HexEntry> newHexes = new HashMap<>(this.hexes);
        newHexes.put(coordinate, newEntry);
        return new ResearchNoteComponent(this.research, this.color, this.complete, Collections.unmodifiableMap(newHexes));
    }
}

