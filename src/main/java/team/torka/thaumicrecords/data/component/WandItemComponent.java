package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import team.torka.thaumicrecords.api.aspect.AspectList;

public record WandItemComponent(String rod, String cap, AspectList aspects) {
    public static final Codec<WandItemComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("rod")
                                    .forGetter(WandItemComponent::getRodKey)
                            , Codec.STRING.fieldOf("cap")
                                    .forGetter(WandItemComponent::getCapKey), AspectList.CODEC.optionalFieldOf(
                                            "aspects", new AspectList())
                                    .forGetter(WandItemComponent::getAspects))
                    .apply(instance, WandItemComponent::new));

    public static final StreamCodec<ByteBuf, WandItemComponent> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, WandItemComponent::getRodKey, ByteBufCodecs.STRING_UTF8,
                    WandItemComponent::getCapKey, AspectList.STREAM_CODEC, WandItemComponent::getAspects,
                    WandItemComponent::new);


    public AspectList getAspects() {
        return aspects;
    }

    public String getRodKey() {
        return rod;
    }

    public String getCapKey() {
        return cap;
    }
}
