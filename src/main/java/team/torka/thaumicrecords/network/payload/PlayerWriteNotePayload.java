package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

public record PlayerWriteNotePayload(BlockPos blockPos, String coordinate, ResourceLocation aspect) implements CustomPacketPayload {
    public static final Type<PlayerWriteNotePayload> TYPE = new Type<>(ThaumicRecords.createRl("player_write_note"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerWriteNotePayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            PlayerWriteNotePayload::blockPos, ByteBufCodecs.STRING_UTF8, PlayerWriteNotePayload::coordinate, ResourceLocation.STREAM_CODEC,
            PlayerWriteNotePayload::aspect, PlayerWriteNotePayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
