package team.torka.thaumicrecords.api.aspect;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;


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
        return BigDecimal.valueOf(get(aspectRl))
                .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public String getScaledOrDefault(ResourceLocation aspectRl, Integer defaultValue) {
        return BigDecimal.valueOf(getOrDefault(aspectRl, defaultValue))
                .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map<?, ?> that)) return false;
        return this.size() == that.size() && this.entrySet().equals(that.entrySet());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

