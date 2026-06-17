package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.world.biome.BiomeRegistry;

import java.util.List;

public class BiomeModifiers {

    protected static final ResourceKey<BiomeModifier> ADD_AER_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_aer_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_IGNIS_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_ignis_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_TERRA_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_terra_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_AQUA_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_aqua_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_ORDO_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_ordo_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_PERDITIO_INFUSED_STONE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_perdito_infused_stone"));
    protected static final ResourceKey<BiomeModifier> ADD_AMBER_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_amber_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_CINNABAR_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_cinnabar_ore"));

    // This modifier serves as a datagen anchor for the magical forest biome.
    // The biome is added to worldgen via MultiNoiseBiomeSourceParameterListMixin at runtime,
    // but during datagen it needs a registry entry that references it (biomes.getOrThrow).
    // Without this, RegistrySetBuilder.reportNotCollectedHolders() would flag it as unreferenced.
    protected static final ResourceKey<BiomeModifier> MAGICAL_FOREST_SPAWNS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("magical_forest_spawns"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedGetter = context.lookup(Registries.PLACED_FEATURE);

        HolderSet.Named<Biome> overworldHolder = biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD);

        context.register(ADD_AER_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AER_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_IGNIS_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_IGNIS_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TERRA_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_TERRA_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_AQUA_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AQUA_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORDO_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_ORDO_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_PERDITIO_INFUSED_STONE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_PERDITIO_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_AMBER_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AMBER_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_CINNABAR_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_CINNABAR_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        // Magical forest mob spawns — also serves as datagen anchor so the biome is "collected"
        HolderSet.Direct<Biome> magicalForestHolder = HolderSet.direct(biomeGetter.getOrThrow(BiomeRegistry.MAGICAL_FOREST));
        context.register(MAGICAL_FOREST_SPAWNS, new net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier(
                magicalForestHolder,
                List.of(
                        new MobSpawnSettings.SpawnerData(EntityType.WOLF, 2, 1, 3),
                        new MobSpawnSettings.SpawnerData(EntityType.HORSE, 2, 1, 3),
                        new MobSpawnSettings.SpawnerData(EntityType.WITCH, 3, 1, 1),
                        new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 3, 1, 1)
                        // TODO: add Pech and Wisp when entities are implemented
                )
        ));
    }
}
