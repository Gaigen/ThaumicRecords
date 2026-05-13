package team.torka.thaumicrecords.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.FeatureRegistry;

public class ConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVERWOOD_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("silverwood_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> GREATWOOD_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ThaumicRecords.createRl("greatwood_tree"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SILVERWOOD_TREE, new ConfiguredFeature<>(FeatureRegistry.SILVERWOOD_TREE.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(GREATWOOD_TREE, new ConfiguredFeature<>(FeatureRegistry.GREATWOOD_TREE.get(), NoneFeatureConfiguration.INSTANCE));
    }

}