package team.torka.thaumicrecords.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.AspectDiscovery;

public record SyncAspectDiscoveryPayload(AspectDiscovery data) implements CustomPacketPayload {

    public static final Type<SyncAspectDiscoveryPayload> TYPE = new Type<>(ThaumicRecords.createRl("sync_aspect_discovery"));

    public static final StreamCodec<ByteBuf, SyncAspectDiscoveryPayload> STREAM_CODEC = StreamCodec.composite(AspectDiscovery.STREAM_CODEC,
            SyncAspectDiscoveryPayload::data, SyncAspectDiscoveryPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}