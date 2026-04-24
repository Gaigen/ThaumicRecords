package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import team.torka.thaumicrecords.api.aspect.AspectList;

public record AspectListComponent(AspectList aspects) {
    public static final Codec<AspectListComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(AspectList.CODEC.optionalFieldOf("aspects", new AspectList()).forGetter(AspectListComponent::getAspects))
                    .apply(instance, AspectListComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AspectListComponent> STREAM_CODEC = StreamCodec.composite(AspectList.STREAM_CODEC,
            AspectListComponent::aspects, AspectListComponent::new);

    public AspectList getAspects() {
        return aspects;
    }
}
