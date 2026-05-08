package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

public record PlayerEraseNotePayload(BlockPos blockPos, String coordinate) implements CustomPacketPayload {
    public static final Type<PlayerEraseNotePayload> TYPE = new Type<>(ThaumicRecords.createRl("player_erase_note"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerEraseNotePayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            PlayerEraseNotePayload::blockPos, ByteBufCodecs.STRING_UTF8, PlayerEraseNotePayload::coordinate, PlayerEraseNotePayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
