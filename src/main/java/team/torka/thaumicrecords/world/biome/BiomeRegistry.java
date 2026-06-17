package team.torka.thaumicrecords.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import team.torka.thaumicrecords.ThaumicRecords;

public class BiomeRegistry {

    public static final ResourceKey<Biome> MAGICAL_FOREST = ResourceKey.create(
            Registries.BIOME, ThaumicRecords.createRl("magical_forest"));

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(MAGICAL_FOREST, magicalForest(placedFeatures, worldCarvers));
    }

    private static Biome magicalForest(HolderGetter<PlacedFeature> placedFeatures,
                                        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        // Mob spawns are added via BiomeModifiers (AddSpawnsBiomeModifier)

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);

        // --- LAKES (step 1) ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.LAKES, "lake_lava_underground");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.LAKES, "lake_lava_surface");

        // --- LOCAL_MODIFICATIONS (step 2) ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.LOCAL_MODIFICATIONS, "amethyst_geode");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.LOCAL_MODIFICATIONS, "forest_rock");

        // --- UNDERGROUND_STRUCTURES (step 3) ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, "monster_room");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, "monster_room_deep");

        // --- UNDERGROUND_ORES (step 6) — same as vanilla forest ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_dirt");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_gravel");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_granite_upper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_granite_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diorite_upper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diorite_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_andesite_upper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_andesite_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_tuff");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_coal_upper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_coal_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_iron_upper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_iron_middle");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_iron_small");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_gold");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_gold_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_redstone");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_redstone_lower");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diamond");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diamond_medium");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diamond_large");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_diamond_buried");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_lapis");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_lapis_buried");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "ore_copper");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "underwater_magma");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "disk_sand");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "disk_clay");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.UNDERGROUND_ORES, "disk_gravel");

        // --- FLUID_SPRINGS (step 8) ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.FLUID_SPRINGS, "spring_water");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.FLUID_SPRINGS, "spring_lava");

        // --- VEGETAL_DECORATION (step 9) — forest surface features ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "glow_lichen");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "forest_flowers");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "flower_default");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "patch_grass_forest");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "patch_tall_grass");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "patch_waterlily");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "brown_mushroom_normal");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "red_mushroom_normal");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "mushroom_island_vegetation");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "patch_sugar_cane");
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.VEGETAL_DECORATION, "patch_pumpkin");

        // --- TOP_LAYER_MODIFICATION (step 10) ---
        addFeature(genBuilder, placedFeatures, GenerationStep.Decoration.TOP_LAYER_MODIFICATION, "freeze_top_layer");

        // Trees are added via NeoForge biome modifier JSONs (src/main/resources/data/...)
        // Silverwood, Greatwood, Aura Node, Magical Oak — through add_features biome modifiers

        // Special effects - colors from original TC4
        BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder()
                .skyColor(0x7BA4FF)
                .fogColor(0xC0D8FF)
                .waterColor(0x0077EE)
                .waterFogColor(0x050533)
                .grassColorOverride(0x55FF81)
                .foliageColorOverride(0x66FFC5);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.6F)
                .specialEffects(effectsBuilder.build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(genBuilder.build())
                .build();
    }

    private static void addFeature(BiomeGenerationSettings.Builder genBuilder,
                                    HolderGetter<PlacedFeature> placedFeatures,
                                    GenerationStep.Decoration step,
                                    String featureName) {
        ResourceKey<PlacedFeature> key = ResourceKey.create(
                Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath("minecraft", featureName));
        Holder.Reference<PlacedFeature> holder = placedFeatures.getOrThrow(key);
        genBuilder.addFeature(step, holder);
    }
}
