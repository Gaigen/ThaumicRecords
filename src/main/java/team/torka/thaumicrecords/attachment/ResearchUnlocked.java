package team.torka.thaumicrecords.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.registry.ResearchCategoryRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record ResearchUnlocked(Set<ResourceLocation> discoveredCategories, Set<ResourceLocation> discoveredResearches,
                               Set<ResourceLocation> completedResearches) {
    public static final ResearchUnlocked EMPTY = new ResearchUnlocked(new HashSet<>(), new HashSet<>(), new HashSet<>());

    /**
     * 从注册表计算默认初始值：initialDiscovered类别、ALWAYS发现策略的研究、INITIAL解锁策略的研究，
     * 并级联发现PARENT策略的研究（当前置研究全部已完成时）
     */
    public static ResearchUnlocked createDefault() {
        Set<ResourceLocation> categories = new HashSet<>();
        ResearchCategoryRegistry.RESEARCH_REGISTRY.forEach(category -> {
            if (category.initialDiscovered) {
                ResourceLocation key = ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(category);
                if (key != null) {
                    categories.add(key);
                }
            }
        });

        Set<ResourceLocation> discovered = new HashSet<>();
        Set<ResourceLocation> completed = new HashSet<>();
        ResearchRegistry.RESEARCH_REGISTRY.forEach(research -> {
            ResourceLocation key = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
            if (key == null) {
                return;
            }
            if (research.discoveryStrategy.contains(Research.DiscoveryStrategy.ALWAYS)) {
                discovered.add(key);
            }
            if (research.unlockStrategy == Research.UnlockStrategy.INITIAL) {
                completed.add(key);
            }
        });

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Research research : ResearchRegistry.RESEARCH_REGISTRY) {
                ResourceLocation key = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
                if (key == null || discovered.contains(key) || completed.contains(key)) {
                    continue;
                }
                if (research.discoveryStrategy.contains(Research.DiscoveryStrategy.PARENT) && areParentsCompleted(research, completed)) {
                    discovered.add(key);
                    changed = true;
                }
            }
        }

        return new ResearchUnlocked(categories, discovered, completed);
    }

    /**
     * 检查研究的所有前置研究是否全部已完成
     */
    public static boolean areParentsCompleted(Research research, Set<ResourceLocation> completed) {
        for (ResourceLocation parent : research.parents) {
            if (!completed.contains(parent)) {
                return false;
            }
        }
        return true;
    }

    public static final Codec<ResearchUnlocked> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.listOf().xmap(HashSet::new,
                    ArrayList::new).fieldOf("discovered_categories").forGetter(d -> new HashSet<>(d.discoveredCategories())),
            ResourceLocation.CODEC.listOf().xmap(
                    HashSet::new, ArrayList::new).fieldOf("discovered_researches").forGetter(d -> new HashSet<>(d.discoveredResearches())),
            ResourceLocation.CODEC.listOf()
                    .xmap(HashSet::new, ArrayList::new)
                    .fieldOf("completed_researches")
                    .forGetter(d -> new HashSet<>(d.completedResearches()))).apply(instance, ResearchUnlocked::new));

    public static final StreamCodec<ByteBuf, ResearchUnlocked> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ResearchUnlocked::discoveredCategories,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ResearchUnlocked::discoveredResearches,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ResearchUnlocked::completedResearches, ResearchUnlocked::new);

    public boolean isCategoryDiscovered(ResourceLocation category) {
        return discoveredCategories.contains(category);
    }

    public boolean isResearchDiscovered(ResourceLocation research) {
        return discoveredResearches.contains(research) || completedResearches.contains(research);
    }

    public boolean isResearchCompleted(ResourceLocation research) {
        return completedResearches.contains(research);
    }

    public ResearchUnlocked discoverCategory(ResourceLocation category) {
        if (discoveredCategories.contains(category)) {
            return this;
        }
        HashSet<ResourceLocation> newCategories = new HashSet<>(discoveredCategories);
        newCategories.add(category);
        return new ResearchUnlocked(newCategories, discoveredResearches, completedResearches);
    }

    public ResearchUnlocked discoverResearch(ResourceLocation research) {
        if (discoveredResearches.contains(research) || completedResearches.contains(research)) {
            return this;
        }
        HashSet<ResourceLocation> newDiscovered = new HashSet<>(discoveredResearches);
        newDiscovered.add(research);
        return new ResearchUnlocked(discoveredCategories, newDiscovered, completedResearches);
    }

    public ResearchUnlocked completeResearch(ResourceLocation research) {
        HashSet<ResourceLocation> newCompleted = new HashSet<>(completedResearches);
        newCompleted.add(research);
        HashSet<ResourceLocation> newDiscovered = new HashSet<>(discoveredResearches);
        newDiscovered.remove(research);
        return new ResearchUnlocked(discoveredCategories, newDiscovered, newCompleted);
    }
}
