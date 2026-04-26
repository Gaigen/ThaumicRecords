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

    public WandItemComponent withAspects(AspectList newAspects) {
        return new WandItemComponent(this.rod, this.cap, newAspects);
    }

    public WandItemComponent addVis(ResourceLocation aspect, int amount, int maxVis) {
        AspectList newAspects = this.aspects.copy();
        int current = newAspects.getOrDefault(aspect, 0);
        newAspects.put(aspect, Math.min(maxVis, current + amount));
        return withAspects(newAspects);
    }

    public WandItemComponent consumeVis(ResourceLocation aspect, int amount) {
        AspectList newAspects = this.aspects.copy();
        int current = newAspects.getOrDefault(aspect, 0);
        newAspects.put(aspect, Math.max(0, current - amount));
        return withAspects(newAspects);
    }

    public WandItemComponent consumeVis(AspectList aspectList) {
        AspectList newAspects = this.aspects.copy();
        aspectList.forEach((aspect, amount) -> {
            int current = newAspects.getOrDefault(aspect, 0);
            newAspects.put(aspect, Math.max(0, current - amount));
        });
        return withAspects(newAspects);
    }
}
