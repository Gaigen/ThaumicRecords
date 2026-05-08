package team.torka.thaumicrecords.network.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.network.payload.PlayerWriteNotePayload;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import java.util.Objects;

public class PlayerWriteNoteHandler {
    public static void handle(PlayerWriteNotePayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            BlockEntity be = player.level().getBlockEntity(payload.blockPos());
            if (be instanceof ResearchTableBlockEntity table) {
                ResourceLocation rl = payload.aspect();
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.isNull(aspect)) {
                    return;
                }
                if (table.canWrite()) {
                    ItemStack researchNote = table.getResearchNotes();
                    if (researchNote.is(ItemRegistry.RESEARCH_NOTES)) {
                        ResearchNoteComponent researchNoteComponent = researchNote.get(DataComponentRegistry.RESEARCH_NOTE);
                        if (Objects.nonNull(researchNoteComponent)) {
                            if (researchNoteComponent.canWriteTo(payload.coordinate())) {
                                ResearchNoteComponent.HexEntry newEntry = new ResearchNoteComponent.HexEntry(ResearchNoteComponent.HexEntry.FULL,
                                        payload.aspect());
                                ResearchNoteComponent temp = researchNoteComponent.writeHex(payload.coordinate(), newEntry);
                                researchNote.set(DataComponentRegistry.RESEARCH_NOTE.get(), temp.finishedOrSelf());
                                table.consumeScribingToolDurability();
                                table.setChanged();
                            }
                        }
                    }
                }
            }
        });
    }

}
