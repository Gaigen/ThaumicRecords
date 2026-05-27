package team.torka.thaumicrecords.api.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.attachment.ResearchUnlocked;
import team.torka.thaumicrecords.attachment.ScanHistory;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ResearchHelper {
    public static void modifyResearchPoint(ServerPlayer player, AspectList add) {
        ResearchPoint oldData = player.getData(AttachmentRegistry.RESEARCH_POINT);
        AspectList points = oldData.points().copy();
        points.merge(add);
        points.forEach((k, v) -> {
            if (v < 0) {
                points.put(k, 0);
            }
        });
        ResearchPoint newData = new ResearchPoint(points);
        player.setData(AttachmentRegistry.RESEARCH_POINT, newData);
    }

    public static void addScannedItem(ServerPlayer player, ResourceLocation itemRl) {
        ScanHistory scanHistory = player.getData(AttachmentRegistry.SCAN_HISTORY);
        scanHistory.addScannedItem(itemRl);
        player.setData(AttachmentRegistry.SCAN_HISTORY, scanHistory);
    }

    public static void unlockResearch(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked oldData = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        if (!oldData.researches().contains(research)) {
            Set<ResourceLocation> oldSet = oldData.researches();
            HashSet<ResourceLocation> newSet = new HashSet<>(oldSet);
            newSet.add(research);
            player.setData(AttachmentRegistry.RESEARCH_UNLOCKED, new ResearchUnlocked(newSet));
        }
    }

    public static boolean isResearchUnlocked(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked data = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        Research research1 = ResearchRegistry.RESEARCH_REGISTRY.get(research);
        if (Objects.isNull(research1)) {
            return false;
        }
        return data.researches().contains(research);
    }
}
