package team.torka.thaumicrecords.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;


public record PlayerUnlockResearchPayload(ResourceLocation research) implements CustomPacketPayload {
    public static final Type<PlayerUnlockResearchPayload> TYPE = new Type<>(ThaumicRecords.createRl("player_unlock_research"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerUnlockResearchPayload> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC,
            PlayerUnlockResearchPayload::research, PlayerUnlockResearchPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
