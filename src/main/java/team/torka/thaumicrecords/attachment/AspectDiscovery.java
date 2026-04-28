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

public record AspectDiscovery(Set<ResourceLocation> discovered) {
    public static final AspectDiscovery DEFAULT = defaultDiscovery();

    public static final Codec<AspectDiscovery> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.listOf().xmap(HashSet::new,
                    ArrayList::new).fieldOf("discovered").forGetter(d -> new HashSet<>(d.discovered())))
            .apply(instance, list -> new AspectDiscovery(new HashSet<>(list))));

    public static final StreamCodec<ByteBuf, AspectDiscovery> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), AspectDiscovery::discovered, AspectDiscovery::new);

    private static AspectDiscovery defaultDiscovery() {
        return new AspectDiscovery(new HashSet<>(Aspect.getPrimalList()));
    }
}
