package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.FeatureRegistry;

import java.util.List;

public class ConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVERWOOD_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("silverwood_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> GREATWOOD_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("greatwood_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> AER_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("aer_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> IGNIS_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("ignis_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> TERRA_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("terra_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> AQUA_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("aqua_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORDO_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("ordo_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> PERDITIO_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("perditio_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("amber_ore"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> CINNABAR_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("cinnabar_ore"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SILVERWOOD_TREE, new ConfiguredFeature<>(FeatureRegistry.SILVERWOOD_TREE.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(GREATWOOD_TREE, new ConfiguredFeature<>(FeatureRegistry.GREATWOOD_TREE.get(), NoneFeatureConfiguration.INSTANCE));

        RuleTest stoneRuleTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateRuleTest = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> aerOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.AER_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.AER_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> ignisOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.IGNIS_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.IGNIS_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> terraOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.TERRA_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.TERRA_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> aquaOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.AQUA_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.AQUA_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> ordoOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.ORDO_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.ORDO_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> perditoOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.PERDITIO_INFUSED_STONE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.PERDITIO_INFUSED_STONE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> amberOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.AMBER_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.AMBER_ORE.get().defaultBlockState())
        );
        List<OreConfiguration.TargetBlockState> cinnabarOreReplacableList = List.of(
                OreConfiguration.target(stoneRuleTest, BlockRegistry.CINNABAR_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateRuleTest, BlockRegistry.CINNABAR_ORE.get().defaultBlockState())
        );

        FeatureUtils.register(context, AER_ORE, Feature.ORE, new OreConfiguration(aerOreReplacableList, 8));
        FeatureUtils.register(context, IGNIS_ORE, Feature.ORE, new OreConfiguration(ignisOreReplacableList, 8));
        FeatureUtils.register(context, TERRA_ORE, Feature.ORE, new OreConfiguration(terraOreReplacableList, 8));
        FeatureUtils.register(context, AQUA_ORE, Feature.ORE, new OreConfiguration(aquaOreReplacableList, 8));
        FeatureUtils.register(context, ORDO_ORE, Feature.ORE, new OreConfiguration(ordoOreReplacableList, 8));
        FeatureUtils.register(context, PERDITIO_ORE, Feature.ORE, new OreConfiguration(perditoOreReplacableList, 8));
        FeatureUtils.register(context, AMBER_ORE, Feature.ORE, new OreConfiguration(amberOreReplacableList, 2));
        FeatureUtils.register(context, CINNABAR_ORE, Feature.ORE, new OreConfiguration(cinnabarOreReplacableList, 2));
    }

}