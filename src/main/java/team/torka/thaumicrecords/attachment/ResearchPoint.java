package team.torka.thaumicrecords.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;

public record ResearchPoint(AspectList points) {
    public static final ResearchPoint DEFAULT = defaultResearchPoint();

    public static final Codec<ResearchPoint> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(AspectList.CODEC.fieldOf("points").forGetter(ResearchPoint::points)).apply(instance, ResearchPoint::new));
    public static final StreamCodec<ByteBuf, ResearchPoint> STREAM_CODEC = StreamCodec.composite(AspectList.STREAM_CODEC, ResearchPoint::points,
            ResearchPoint::new);

    private static ResearchPoint defaultResearchPoint() {
        AspectList aspectList = new AspectList();
        for (ResourceLocation rl : Aspect.getPrimalList()) {
            aspectList.add(rl, 20);
        }
        return new ResearchPoint(aspectList);
    }
}