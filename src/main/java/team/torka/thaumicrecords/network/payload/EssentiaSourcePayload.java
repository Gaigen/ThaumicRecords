package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

/**
 * Packet sent from server to client when essentia is drained from a jar.
 * Triggers a visual particle effect traveling from the jar to the matrix.
 */
public record EssentiaSourcePayload(BlockPos matrixPos, BlockPos jarPos, int color) implements CustomPacketPayload {

    public static final Type<EssentiaSourcePayload> TYPE = new Type<>(ThaumicRecords.createRl("essentia_source"));

    public static final StreamCodec<FriendlyByteBuf, EssentiaSourcePayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            EssentiaSourcePayload::matrixPos, BlockPos.STREAM_CODEC, EssentiaSourcePayload::jarPos, net.minecraft.network.codec.ByteBufCodecs.INT,
            EssentiaSourcePayload::color, EssentiaSourcePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
