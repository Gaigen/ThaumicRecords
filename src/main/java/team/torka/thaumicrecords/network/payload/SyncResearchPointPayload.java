package team.torka.thaumicrecords.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.ResearchPoint;

public record SyncResearchPointPayload(ResearchPoint data) implements CustomPacketPayload {
    public static final Type<SyncResearchPointPayload> TYPE = new Type<>(ThaumicRecords.createRl("sync_points"));

    public static final StreamCodec<ByteBuf, SyncResearchPointPayload> STREAM_CODEC = StreamCodec.composite(ResearchPoint.STREAM_CODEC,
            SyncResearchPointPayload::data, SyncResearchPointPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
