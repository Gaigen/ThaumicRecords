package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.research.ResearchCategory;

@EventBusSubscriber
public class ResearchCategoryRegistry {
    public static final DeferredRegister<ResearchCategory> REGISTRAR = DeferredRegister.create(RegistryKeys.RESEARCH_CATEGORIES, ThaumicRecords.MOD_ID);
    public static Registry<ResearchCategory> RESEARCH_REGISTRY = null;

    public static final DeferredHolder<ResearchCategory, ResearchCategory> BASIC = REGISTRAR.register("basic",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "basic"),
                    ThaumicRecords.createRl("textures/research/icon/thaumonomiconcheat.png"), null, ResearchCategory.DEFAULT_BGTEX));

    public static final DeferredHolder<ResearchCategory, ResearchCategory> THAUMATURGY = REGISTRAR.register("thaumaturgy",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "thaumaturgy"),
                    ThaumicRecords.createRl("textures/research/icon/thaumaturgy.png"), null, ResearchCategory.DEFAULT_BGTEX));

    public static final DeferredHolder<ResearchCategory, ResearchCategory> ALCHEMY = REGISTRAR.register("alchemy",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "alchemy"),
                    ThaumicRecords.createRl("textures/research/icon/crucible.png"), null, ResearchCategory.DEFAULT_BGTEX));

    public static final DeferredHolder<ResearchCategory, ResearchCategory> ARTIFICE = REGISTRAR.register("artifice",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "artifice"),
                    ThaumicRecords.createRl("textures/research/icon/artifice.png"), null, ResearchCategory.DEFAULT_BGTEX));

    public static final DeferredHolder<ResearchCategory, ResearchCategory> GOLEMANCY = REGISTRAR.register("golemancy",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "golemancy"),
                    ThaumicRecords.createRl("textures/research/icon/golemancy.png"), null, ResearchCategory.DEFAULT_BGTEX));

    public static final DeferredHolder<ResearchCategory, ResearchCategory> ELDRITCH = REGISTRAR.register("eldritch",
            () -> new ResearchCategory(ThaumicRecords.createTranslationKey("research_category", "eldritch"),
                    ThaumicRecords.createRl("textures/research/icon/eldritch.png"), null,
                    ThaumicRecords.createRl("textures/research/background/eldritch.png")));

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCH_CATEGORIES));
    }

}
