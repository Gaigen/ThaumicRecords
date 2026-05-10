package team.torka.thaumicrecords.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record ResearchUnlocked(Set<ResourceLocation> researches) {
    public static final ResearchUnlocked DEFAULT = defaultDiscovery();

    public static final Codec<ResearchUnlocked> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.listOf().xmap(HashSet::new,
                    ArrayList::new).fieldOf("researches").forGetter(d -> new HashSet<>(d.researches())))
            .apply(instance, list -> new ResearchUnlocked(new HashSet<>(list))));

    public static final StreamCodec<ByteBuf, ResearchUnlocked> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ResearchUnlocked::researches, ResearchUnlocked::new);

    private static ResearchUnlocked defaultDiscovery() {
        return new ResearchUnlocked(new HashSet<>(Aspect.getPrimalList()));
    }
}
