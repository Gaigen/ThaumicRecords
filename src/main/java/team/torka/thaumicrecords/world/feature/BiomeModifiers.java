package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.torka.thaumicrecords.ThaumicRecords;

public class BiomeModifiers {

    protected static final ResourceKey<BiomeModifier> ADD_AER_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_aer_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_IGNIS_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_ignis_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_TERRA_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_terra_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_AQUA_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_aqua_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_ORDO_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_ordo_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_PERDITIO_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_perdito_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_AMBER_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_amber_ore"));
    protected static final ResourceKey<BiomeModifier> ADD_CINNABAR_ORE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ThaumicRecords.createRl("add_cinnabar_ore"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedGetter = context.lookup(Registries.PLACED_FEATURE);

        HolderSet.Named<Biome> overworldHolder = biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD);

        context.register(ADD_AER_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AER_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_IGNIS_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_IGNIS_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_TERRA_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_TERRA_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_AQUA_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AQUA_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_ORDO_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_ORDO_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_PERDITIO_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_PERDITIO_INFUSED_STONE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_AMBER_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_AMBER_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_CINNABAR_ORE, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(overworldHolder,
                HolderSet.direct(placedGetter.getOrThrow(PlacedFeatures.PLACED_CINNABAR_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES));
    }
}
