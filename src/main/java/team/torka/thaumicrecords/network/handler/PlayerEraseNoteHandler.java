package team.torka.thaumicrecords.network.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.network.payload.PlayerEraseNotePayload;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import java.util.Objects;

public class PlayerEraseNoteHandler {
    public static void handle(PlayerEraseNotePayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            BlockEntity be = player.level().getBlockEntity(payload.blockPos());
            if (be instanceof ResearchTableBlockEntity table) {
                if (table.canWrite()) {
                    ItemStack researchNote = table.getResearchNotes();
                    if (researchNote.is(ItemRegistry.RESEARCH_NOTES)) {
                        ResearchNoteComponent researchNoteComponent = researchNote.get(DataComponentRegistry.RESEARCH_NOTE);
                        if (Objects.nonNull(researchNoteComponent)) {
                            if (researchNoteComponent.hexes().containsKey(payload.coordinate())) {
                                ResearchNoteComponent.HexEntry hexEntry = researchNoteComponent.hexes().get(payload.coordinate());
                                if (hexEntry.type() == ResearchNoteComponent.HexEntry.FULL) {
                                    ResearchNoteComponent.HexEntry newEntry = new ResearchNoteComponent.HexEntry(ResearchNoteComponent.HexEntry.EMPTY, null);
                                    researchNote.set(DataComponentRegistry.RESEARCH_NOTE.get(), researchNoteComponent.writeHex(payload.coordinate(), newEntry));
                                    table.consumeScribingToolDurability();
                                    table.setChanged();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
