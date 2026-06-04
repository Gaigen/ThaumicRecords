package team.torka.thaumicrecords.api.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.api.research.ResearchCategory;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.attachment.ResearchUnlocked;
import team.torka.thaumicrecords.attachment.ScanHistory;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.ResearchCategoryRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static void discoverResearch(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked oldData = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        ResearchUnlocked newData = oldData.discoverResearch(research);
        if (newData != oldData) {
            player.setData(AttachmentRegistry.RESEARCH_UNLOCKED, newData);
        }
    }

    public static void completeResearch(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked oldData = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        ResearchUnlocked newData = oldData.completeResearch(research);
        newData = cascadeDiscoverChildren(newData, research);
        if (!newData.equals(oldData)) {
            player.setData(AttachmentRegistry.RESEARCH_UNLOCKED, newData);
        }
    }

    private static ResearchUnlocked cascadeDiscoverChildren(ResearchUnlocked data, ResourceLocation completedResearch) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Research research : ResearchRegistry.RESEARCH_REGISTRY) {
                ResourceLocation key = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
                if (key == null || data.isResearchDiscovered(key)) {
                    continue;
                }
                if (research.discoveryStrategy.contains(Research.DiscoveryStrategy.PARENT) && ResearchUnlocked.areParentsCompleted(research,
                        data.completedResearches())) {
                    data = data.discoverResearch(key);
                    changed = true;
                }
            }
        }
        return data;
    }

    public static void discoverCategory(ServerPlayer player, ResourceLocation category) {
        ResearchUnlocked oldData = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        ResearchUnlocked newData = oldData.discoverCategory(category);
        if (newData != oldData) {
            player.setData(AttachmentRegistry.RESEARCH_UNLOCKED, newData);
        }
    }

    public static boolean isResearchDiscovered(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked data = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        return data.isResearchDiscovered(research);
    }

    public static boolean isResearchCompleted(ServerPlayer player, ResourceLocation research) {
        ResearchUnlocked data = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        return data.isResearchCompleted(research);
    }

    public static boolean isCategoryDiscovered(ServerPlayer player, ResourceLocation category) {
        ResearchUnlocked data = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        return data.isCategoryDiscovered(category);
    }

    public static List<Research> getResearchesByCategory(ResearchCategory category) {
        ResourceLocation categoryRl = ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(category);
        if (Objects.isNull(categoryRl)) {
            return Collections.emptyList();
        }
        return ResearchRegistry.RESEARCH_REGISTRY.stream().filter(research -> research.category.equals(categoryRl)).collect(Collectors.toList());
    }

}
