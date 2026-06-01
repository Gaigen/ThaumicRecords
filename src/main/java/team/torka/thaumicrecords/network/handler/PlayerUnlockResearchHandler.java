package team.torka.thaumicrecords.network.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.api.helper.ResearchNoteHelper;
import team.torka.thaumicrecords.api.item.ScribingTool;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.attachment.ResearchUnlocked;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.network.payload.PlayerUnlockResearchPayload;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import java.util.Objects;


public class PlayerUnlockResearchHandler {

    public static void handle(PlayerUnlockResearchPayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            Research research = ResearchRegistry.RESEARCH_REGISTRY.get(payload.research());
            if (Objects.isNull(research)) {
                return;
            }

            ResearchUnlocked researchUnlocked = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);

            if (!researchUnlocked.isResearchDiscovered(payload.research())) {
                return;
            }

            if (researchUnlocked.isResearchCompleted(payload.research())) {
                return;
            }

            if (!ResearchUnlocked.areParentsCompleted(research, researchUnlocked.completedResearches())) {
                return;
            }

            if (research.unlockStrategy == Research.UnlockStrategy.POINTS) {
                handlePointsStrategy(player, research, payload.research());
            } else if (research.unlockStrategy == Research.UnlockStrategy.RESEARCH) {
                handleResearchStrategy(player, research, payload.research());
            }
        });
    }

    private static void handlePointsStrategy(ServerPlayer player, Research research, net.minecraft.resources.ResourceLocation researchKey) {
        ResearchPoint rp = player.getData(AttachmentRegistry.RESEARCH_POINT);

        boolean hasEnoughPoints = research.aspects.entrySet().stream().allMatch(entry -> rp.points().getOrZero(entry.getKey()) >= entry.getValue());
        if (!hasEnoughPoints) {
            return;
        }

        team.torka.thaumicrecords.api.aspect.AspectList deduct = new team.torka.thaumicrecords.api.aspect.AspectList();
        research.aspects.forEach((aspect, cost) -> deduct.put(aspect, -cost));
        ResearchHelper.modifyResearchPoint(player, deduct);

        ResearchHelper.completeResearch(player, researchKey);
    }


    private static void handleResearchStrategy(ServerPlayer player, Research research, net.minecraft.resources.ResourceLocation researchKey) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemRegistry.RESEARCH_NOTES.get())) {
                ResearchNoteComponent noteData = stack.get(DataComponentRegistry.RESEARCH_NOTE);
                if (noteData != null && noteData.research().equals(researchKey)) {
                    return;
                }
            }
        }

        boolean hasScribingTool = false;
        ItemStack scribingToolStack = null;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof ScribingTool scribingTool && scribingTool.canScribe(stack, player)) {
                hasScribingTool = true;
                scribingToolStack = stack;
                break;
            }
        }
        if (!hasScribingTool) {
            return;
        }

        boolean hasPaper = false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.PAPER)) {
                hasPaper = true;
                break;
            }
        }
        if (!hasPaper) {
            return;
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.PAPER)) {
                stack.shrink(1);
                break;
            }
        }

        if (scribingToolStack.getItem() instanceof ScribingTool tool) {
            tool.consumeDurability(scribingToolStack);
        }

        ResearchNoteComponent noteComponent = ResearchNoteHelper.generateNote(research, researchKey, player.getRandom());
        ItemStack noteStack = new ItemStack(ItemRegistry.RESEARCH_NOTES.get());
        noteStack.set(DataComponentRegistry.RESEARCH_NOTE.get(), noteComponent);

        if (!player.getInventory().add(noteStack)) {
            player.drop(noteStack, false);
        }
    }
}
