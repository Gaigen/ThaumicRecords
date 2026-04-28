package team.torka.thaumicrecords.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.AspectDiscovery;

public record SyncAspectDiscoveryPacket(AspectDiscovery data) implements CustomPacketPayload {

    public static final Type<SyncAspectDiscoveryPacket> TYPE = new Type<>(ThaumicRecords.createRl("sync_aspect_discovery"));

    public static final StreamCodec<ByteBuf, SyncAspectDiscoveryPacket> STREAM_CODEC = StreamCodec.composite(AspectDiscovery.STREAM_CODEC,
            SyncAspectDiscoveryPacket::data, SyncAspectDiscoveryPacket::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}