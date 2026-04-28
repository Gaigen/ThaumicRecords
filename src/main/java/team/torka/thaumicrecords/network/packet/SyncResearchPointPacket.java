package team.torka.thaumicrecords.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.ResearchPoint;

public record SyncResearchPointPacket(ResearchPoint data) implements CustomPacketPayload {
    public static final Type<SyncResearchPointPacket> TYPE = new Type<>(ThaumicRecords.createRl("sync_points"));

    public static final StreamCodec<ByteBuf, SyncResearchPointPacket> STREAM_CODEC = StreamCodec.composite(ResearchPoint.STREAM_CODEC,
            SyncResearchPointPacket::data, SyncResearchPointPacket::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
