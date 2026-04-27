package team.torka.thaumicrecords.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import team.torka.thaumicrecords.ThaumicRecords;

public class ModTags {

    public static final TagKey<Item> SHOW_NODE_WHEN_EQUIPPED = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("show_node_when_equipped"));
    public static final TagKey<Item> SHOW_NODE_WHEN_HELD = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("show_node_when_held"));
    public static final TagKey<Item> WAND_CAPS = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("wand_caps"));
    public static final TagKey<Item> WAND_RODS = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("wand_rods"));
    public static final TagKey<Item> SCRIBING_TOOLS = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("scribing_tools"));
}
