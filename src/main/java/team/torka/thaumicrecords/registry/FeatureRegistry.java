package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.world.feature.AuraNodeFeature;
import team.torka.thaumicrecords.world.feature.GreatwoodTreeFeature;
import team.torka.thaumicrecords.world.feature.MagicalOakTreeFeature;
import team.torka.thaumicrecords.world.feature.SilverwoodTreeFeature;

public class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> REGISTRAR = DeferredRegister.create(Registries.FEATURE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<Feature<?>, AuraNodeFeature> AURA_NODE = REGISTRAR.register("aura_node",
            () -> new AuraNodeFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, SilverwoodTreeFeature> SILVERWOOD_TREE = REGISTRAR.register("silverwood_tree",
            () -> new SilverwoodTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, GreatwoodTreeFeature> GREATWOOD_TREE = REGISTRAR.register("greatwood_tree",
            () -> new GreatwoodTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, MagicalOakTreeFeature> MAGICAL_OAK_TREE = REGISTRAR.register("magical_oak_tree",
            () -> new MagicalOakTreeFeature(NoneFeatureConfiguration.CODEC));
}
