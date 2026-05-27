package team.torka.thaumicrecords.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record ScanHistory(Set<String> items, Set<String> entities, Set<String> nodes) {

    public static final ScanHistory DEFAULT = defaultScanHistory();

    public static final Codec<ScanHistory> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.listOf()
            .xmap(HashSet::new, ArrayList::new)
            .fieldOf("items")
            .forGetter(s -> new HashSet<>(s.items)), Codec.STRING.listOf()
            .xmap(HashSet::new, ArrayList::new)
            .fieldOf("entities")
            .forGetter(s -> new HashSet<>(s.entities)), Codec.STRING.listOf()
            .xmap(HashSet::new, ArrayList::new)
            .fieldOf("nodes")
            .forGetter(s -> new HashSet<>(s.nodes))).apply(instance, ScanHistory::new));

    public static final StreamCodec<ByteBuf, ScanHistory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(HashSet::new)), ScanHistory::items,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(HashSet::new)), ScanHistory::entities,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(HashSet::new)), ScanHistory::nodes, ScanHistory::new);


    private static ScanHistory defaultScanHistory() {
        return new ScanHistory(new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    public boolean hasScannedItem(String itemIdentifier) {
        return this.items.contains(itemIdentifier);
    }

    public boolean hasScannedItem(ResourceLocation itemRl) {
        return hasScannedItem(itemRl.toString());
    }

    public void addScannedItem(String itemIdentifier) {
        this.items.add(itemIdentifier);
    }

    public void addScannedItem(ResourceLocation itemRl) {
        addScannedItem(itemRl.toString());
    }

    public boolean hasScannedEntity(String entityIdentifier) {
        return this.entities.contains(entityIdentifier);
    }

    public boolean hasScannedEntity(ResourceLocation entityRl) {
        return hasScannedEntity(entityRl.toString());
    }

    public void addScannedEntity(String entityIdentifier) {
        this.entities.add(entityIdentifier);
    }

    public void addScannedEntity(ResourceLocation entityRl) {
        addScannedEntity(entityRl.toString());
    }

    public boolean hasScannedNode(String nodeId) {
        return this.nodes.contains(nodeId);
    }

    public void addScannedNode(String nodeId) {
        this.nodes.add(nodeId);
    }
}
