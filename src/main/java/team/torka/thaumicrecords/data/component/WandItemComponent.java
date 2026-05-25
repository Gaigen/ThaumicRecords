package team.torka.thaumicrecords.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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

    public WandItemComponent addVis(ResourceLocation aspect, int amount, AtomicInteger remain) {
        AspectList newAspects = this.aspects.copy();
        if (!Aspect.getPrimalList().contains(aspect)) {
            remain.set(amount);
            return this;
        }
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(this.getRod());
        if (Objects.isNull(wandRod)) {
            remain.set(amount);
            return this;
        }
        int current = this.getAspects().getOrZero(aspect);
        int max = wandRod.getCapacity();
        int lack = Math.max(max - current, 0);
        int actualAmountToProcess = amount * 100;
        if (actualAmountToProcess >= lack) {
            int left = actualAmountToProcess - lack;
            remain.set(left / 100);
            newAspects.put(aspect, max);
        } else {
            remain.set(0);
            newAspects.put(aspect, current + actualAmountToProcess);
        }
        return withAspects(newAspects);
    }

    public WandItemComponent consumeVis(ResourceLocation aspect, int amount) {
        AspectList newAspects = this.aspects.copy();
        int current = newAspects.getOrZero(aspect);
        newAspects.put(aspect, Math.max(0, current - amount));
        return withAspects(newAspects);
    }

    public WandItemComponent consumeVis(AspectList aspectList) {
        AspectList newAspects = this.aspects.copy();
        aspectList.forEach((aspect, amount) -> {
            int current = newAspects.getOrZero(aspect);
            newAspects.put(aspect, Math.max(0, current - amount));
        });
        return withAspects(newAspects);
    }

    public Integer getCapacity() {
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(this.getRod());
        if (Objects.isNull(wandRod)) {
            return 0;
        }
        return wandRod.getCapacity();
    }

    public List<ResourceLocation> getLackVisAspect() {
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(this.getRod());
        if (Objects.isNull(wandRod)) {
            return Collections.emptyList();
        }
        int max = wandRod.getCapacity();
        List<ResourceLocation> result = new ArrayList<>();
        Aspect.getPrimalList().forEach(rl -> {
            if (this.aspects.getOrZero(rl) < max) {
                result.add(rl);
            }
        });
        return result;
    }
}
