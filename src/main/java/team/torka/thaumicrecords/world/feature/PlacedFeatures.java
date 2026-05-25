package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import team.torka.thaumicrecords.ThaumicRecords;

import java.util.List;


public class PlacedFeatures {

    public static final ResourceKey<PlacedFeature> PLACED_AER_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("aer_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_IGNIS_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("ignis_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_TERRA_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("terra_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_AQUA_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("aqua_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_ORDO_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("ordo_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_PERDITIO_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("perditio_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_AMBER_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("amber_ore")));
    public static final ResourceKey<PlacedFeature> PLACED_CINNABAR_ORE = ResourceKey.create(Registries.PLACED_FEATURE,ThaumicRecords.createRl(("cinnabar_ore")));

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, PLACED_AER_ORE, holdergetter.getOrThrow(ConfiguredFeatures.AER_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_IGNIS_ORE, holdergetter.getOrThrow(ConfiguredFeatures.IGNIS_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_TERRA_ORE, holdergetter.getOrThrow(ConfiguredFeatures.TERRA_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_AQUA_ORE, holdergetter.getOrThrow(ConfiguredFeatures.AQUA_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_ORDO_ORE, holdergetter.getOrThrow(ConfiguredFeatures.ORDO_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_PERDITIO_ORE, holdergetter.getOrThrow(ConfiguredFeatures.PERDITIO_ORE),
                commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_AMBER_ORE, holdergetter.getOrThrow(ConfiguredFeatures.AMBER_ORE),
                commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        PlacementUtils.register(context, PLACED_CINNABAR_ORE, holdergetter.getOrThrow(ConfiguredFeatures.CINNABAR_ORE),
                commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(count), modifier);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier1) {
        return List.of(modifier, InSquarePlacement.spread(), modifier1, BiomeFilter.biome());
    }
}
