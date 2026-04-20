package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.aspect.AspectList;

public record WandItemComponent(ResourceLocation rod, ResourceLocation cap, AspectList aspects) {
    public static final Codec<WandItemComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ResourceLocation.CODEC.fieldOf("rod").forGetter(WandItemComponent::getRod),
                            ResourceLocation.CODEC.fieldOf("cap").forGetter(WandItemComponent::getCap),
                            AspectList.CODEC.optionalFieldOf("aspects", new AspectList()).forGetter(WandItemComponent::getAspects))
                    .apply(instance, WandItemComponent::new));

    public static final StreamCodec<ByteBuf, WandItemComponent> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, WandItemComponent::getRod,
            ResourceLocation.STREAM_CODEC, WandItemComponent::getCap, AspectList.STREAM_CODEC, WandItemComponent::getAspects, WandItemComponent::new);


    public AspectList getAspects() {
        return aspects;
    }

    public ResourceLocation getRod() {
        return rod;
    }

    public ResourceLocation getCap() {
        return cap;
    }
}
