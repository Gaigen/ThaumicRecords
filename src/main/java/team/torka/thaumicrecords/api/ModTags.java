package team.torka.thaumicrecords.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import team.torka.thaumicrecords.ThaumicRecords;

public class ModTags {

    public static final TagKey<Item> SHOW_NODE_WHEN_EQUIPPED = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("show_node_when_equipped"));
    public static final TagKey<Item> SHOW_ASPECT_WHEN_EQUIPPED = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("show_aspect_when_equipped"));
    public static final TagKey<Item> SHOW_NODE_WHEN_HELD = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("show_node_when_held"));
    public static final TagKey<Item> SHARD = TagKey.create(Registries.ITEM, ThaumicRecords.createRl("shard"));

    public static final TagKey<Block> CRUCIBLE_HEAT_SOURCE = TagKey.create(Registries.BLOCK, ThaumicRecords.createRl("crucible_heat_source"));
}
