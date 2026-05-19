package team.torka.thaumicrecords.api.aspect;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AspectList extends LinkedHashMap<ResourceLocation, Integer> {
    public static final Codec<AspectList> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).xmap(AspectList::fromMap, aspectList -> aspectList);

    public static final StreamCodec<ByteBuf, AspectList> STREAM_CODEC = ByteBufCodecs.map(LinkedHashMap::new, ResourceLocation.STREAM_CODEC,
            ByteBufCodecs.VAR_INT).map(AspectList::fromMap, aspectList -> aspectList);

    public static AspectList fromMap(Map<ResourceLocation, Integer> map) {
        AspectList aspectList = new AspectList();
        aspectList.putAll(map);
        return aspectList;
    }

    public String getScaled(ResourceLocation aspectRl) {
        return BigDecimal.valueOf(getOrDefault(aspectRl, 0)).setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(100), RoundingMode.HALF_UP).setScale(2,
                RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public static String formatScaled(Integer amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public Tag writeToNBT() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public void readFromNBT(Tag tag) {
        if (Objects.isNull(tag)) {
            return;
        }
        this.clear();
        CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(s -> {
        }).ifPresent(this::putAll);
    }

    public AspectList add(ResourceLocation aspect, int amount) {
        if (this.containsKey(aspect)) {
            this.put(aspect, this.get(aspect) + amount);
        } else {
            this.put(aspect, amount);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map<?, ?> that)) {
            return false;
        }
        return this.size() == that.size() && this.entrySet().equals(that.entrySet());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static AspectList empty() {
        return new AspectList();
    }

    public AspectList copy() {
        return AspectList.fromMap(this);
    }

    public void merge(AspectList aspectList) {
        aspectList.forEach((aspect, amount) -> this.merge(aspect, amount, Integer::sum));
    }

    public AspectList multiply(int multiplier) {
        this.forEach((aspect, amount) -> this.put(aspect, amount * multiplier));
        return this;
    }

    public List<ResourceLocation> getPrimalKey() {
        return this.keySet().stream().filter(e -> Aspect.getPrimalList().contains(e)).toList();
    }

    public int getWithModifier(ResourceLocation rl, Double modifier) {
        int base = this.getOrDefault(rl, 0);
        Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
        if (Objects.isNull(aspect)) {
            return base;
        }
        return (int) (base * (Objects.nonNull(modifier) ? modifier : 1.0));
    }
}

