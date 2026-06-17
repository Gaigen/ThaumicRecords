package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import team.torka.thaumicrecords.ThaumicRecords;

import java.util.List;


public class PlacedFeatures {

    public static final ResourceKey<PlacedFeature> PLACED_AURA_NODE = ResourceKey.create(Registries.PLACED_FEATURE, ThaumicRecords.createRl(("aura_node")));
    public static final ResourceKey<PlacedFeature> PLACED_AER_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("aer_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_IGNIS_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("ignis_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_TERRA_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("terra_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_AQUA_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("aqua_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_ORDO_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("ordo_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_PERDITIO_INFUSED_STONE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("perditio_infused_stone")));
    public static final ResourceKey<PlacedFeature> PLACED_AMBER_ORE = ResourceKey.create(Registries.PLACED_FEATURE, ThaumicRecords.createRl(("amber_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_CINNABAR_ORE = ResourceKey.create(Registries.PLACED_FEATURE,
            ThaumicRecords.createRl(("cinnabar_ore")));

    // Magical Forest biome features
    public static final ResourceKey<PlacedFeature> PLACED_SILVERWOOD_TREE_MAGICAL_FOREST = ResourceKey.create(
            Registries.PLACED_FEATURE, ThaumicRecords.createRl("silverwood_tree_magical_forest"));
    public static final ResourceKey<PlacedFeature> PLACED_GREATWOOD_TREE_MAGICAL_FOREST = ResourceKey.create(
            Registries.PLACED_FEATURE, ThaumicRecords.createRl("greatwood_tree_magical_forest"));
    public static final ResourceKey<PlacedFeature> PLACED_MAGICAL_OAK = ResourceKey.create(
            Registries.PLACED_FEATURE, ThaumicRecords.createRl("magical_oak"));
    public static final ResourceKey<PlacedFeature> PLACED_AURA_NODE_MAGICAL_FOREST = ResourceKey.create(
            Registries.PLACED_FEATURE, ThaumicRecords.createRl("aura_node_magical_forest"));

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, PLACED_AER_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.AER_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_IGNIS_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.IGNIS_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_TERRA_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.TERRA_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_AQUA_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.AQUA_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_ORDO_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.ORDO_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_PERDITIO_INFUSED_STONE, holdergetter.getOrThrow(ConfiguredFeatures.PERDITIO_INFUSED_STONE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_AMBER_ORE, holdergetter.getOrThrow(ConfiguredFeatures.AMBER_ORE),
                commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_CINNABAR_ORE, holdergetter.getOrThrow(ConfiguredFeatures.CINNABAR_ORE),
                commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        // Magical Forest trees - silverwood (1 per 14 chunks, same as original TC4)
        PlacementUtils.register(context, PLACED_SILVERWOOD_TREE_MAGICAL_FOREST,
                holdergetter.getOrThrow(ConfiguredFeatures.SILVERWOOD_TREE),
                List.of(RarityFilter.onAverageOnceEvery(14), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()));

        // Magical Forest trees - greatwood (1 per 10 chunks, same as original TC4)
        PlacementUtils.register(context, PLACED_GREATWOOD_TREE_MAGICAL_FOREST,
                holdergetter.getOrThrow(ConfiguredFeatures.GREATWOOD_TREE),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()));

        // Magical Forest trees - big magic oak (2 per chunk, same as original TC4 treesPerChunk)
        PlacementUtils.register(context, PLACED_MAGICAL_OAK,
                holdergetter.getOrThrow(ConfiguredFeatures.MAGICAL_OAK),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()));

        // Magical Forest aura nodes (1 per 3 chunks)
        PlacementUtils.register(context, PLACED_AURA_NODE_MAGICAL_FOREST,
                holdergetter.getOrThrow(ConfiguredFeatures.AURA_NODE),
                List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()));
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(count), modifier);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier1) {
        return List.of(modifier, InSquarePlacement.spread(), modifier1, BiomeFilter.biome());
    }
}
