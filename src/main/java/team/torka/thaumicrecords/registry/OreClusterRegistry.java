package team.torka.thaumicrecords.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OreClusterRegistry {
    public record ClusterMapping(Supplier<ItemStack> output, float baseChance) {
    }

    private static final Map<Block, ClusterMapping> MAPPINGS = new HashMap<>();

    public static void init() {
        register(() -> Blocks.IRON_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_IRON.get()), 1.0F);
        register(() -> Blocks.DEEPSLATE_IRON_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_IRON.get()), 1.0F);

        register(() -> Blocks.GOLD_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_GOLD.get()), 0.9F);
        register(() -> Blocks.DEEPSLATE_GOLD_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_GOLD.get()), 0.9F);

        register(() -> Blocks.COPPER_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_COPPER.get()), 1.0F);
        register(() -> Blocks.DEEPSLATE_COPPER_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_COPPER.get()), 1.0F);

        register(BlockRegistry.CINNABAR_ORE, () -> new ItemStack(ItemRegistry.CLUSTER_CINNABAR.get()), 0.9F);
    }

    private static void register(Supplier<Block> block, Supplier<ItemStack> output, float chance) {
        MAPPINGS.put(block.get(), new ClusterMapping(output, chance));
    }

    public static ClusterMapping getMapping(Block block) {
        return MAPPINGS.get(block);
    }
}
