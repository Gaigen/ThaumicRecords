package team.torka.thaumicrecords.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.api.node.NodeModifier;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.api.research.ResearchCategory;

public interface RegistryKeys {
    ResourceKey<Registry<Aspect>> ASPECTS = ResourceKey.createRegistryKey(ThaumicRecords.createRl("aspects"));
    ResourceKey<Registry<Research>> RESEARCHES = ResourceKey.createRegistryKey(ThaumicRecords.createRl("researches"));
    ResourceKey<Registry<ResearchCategory>> RESEARCH_CATEGORIES = ResourceKey.createRegistryKey(ThaumicRecords.createRl("research_categories"));
    ResourceKey<Registry<WandCap>> WAND_CAPS = ResourceKey.createRegistryKey(ThaumicRecords.createRl("wand_caps"));
    ResourceKey<Registry<WandRod>> WAND_RODS = ResourceKey.createRegistryKey(ThaumicRecords.createRl("wand_rods"));
    ResourceKey<Registry<NodeType>> NODE_TYPES = ResourceKey.createRegistryKey(ThaumicRecords.createRl("node_types"));
    ResourceKey<Registry<NodeModifier>> NODE_MODIFIERS = ResourceKey.createRegistryKey(ThaumicRecords.createRl("node_modifiers"));
}
