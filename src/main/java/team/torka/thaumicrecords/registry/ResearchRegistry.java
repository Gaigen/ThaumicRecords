package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.research.Research;

import java.util.List;

@EventBusSubscriber
public class ResearchRegistry {
    public static final DeferredRegister<Research> REGISTRAR = DeferredRegister.create(RegistryKeys.RESEARCHES, ThaumicRecords.MOD_ID);
    public static Registry<Research> RESEARCH_REGISTRY = null;

    private static final ResourceLocation CAT_BASIC = ResearchCategoryRegistry.BASIC.getId();

    // ========== BASICS category ==========

    public static final DeferredHolder<Research, Research> ASPECTS = REGISTRAR.register("aspects",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "aspects"), ThaumicRecords.createTranslationKey("research_desc", "aspects"),
                    CAT_BASIC, AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/aspects.png"), null, new ResourceLocation[0], 0, 0, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> PECH = REGISTRAR.register("pech",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "pech"), ThaumicRecords.createTranslationKey("research_desc", "pech"), CAT_BASIC,
                    AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/pech.png"), null, new ResourceLocation[0], -4, -4, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> NODES = REGISTRAR.register("nodes",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "nodes"), ThaumicRecords.createTranslationKey("research_desc", "nodes"),
                    CAT_BASIC, AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/nodes.png"), null, new ResourceLocation[0], -2, 0, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> WARP = REGISTRAR.register("warp",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "warp"), ThaumicRecords.createTranslationKey("research_desc", "warp"), CAT_BASIC,
                    AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/warp.png"), null, new ResourceLocation[0], 0, 2, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> RESEARCH = REGISTRAR.register("research",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "research"), ThaumicRecords.createTranslationKey("research_desc", "research"),
                    CAT_BASIC, AspectList.empty(), null, new ItemStack(ItemRegistry.SCRIBING_TOOLS.get()), new ResourceLocation[0], 2, 0, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> ORE = REGISTRAR.register("ore",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "ore"), ThaumicRecords.createTranslationKey("research_desc", "ore"), CAT_BASIC,
                    AspectList.empty(), null, new ItemStack(ItemRegistry.AMBER_ORE.get()), new ResourceLocation[0], -2, -2, 0, Research.RenderStrategy.ROUND,
                    Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> PLANTS = REGISTRAR.register("plants",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "plants"), ThaumicRecords.createTranslationKey("research_desc", "plants"),
                    CAT_BASIC, AspectList.empty(), null, new ItemStack(ItemRegistry.GREATWOOD_SAPLING.get()), new ResourceLocation[0], -2, -4, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> ENCHANT = REGISTRAR.register("enchant",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "enchant"), ThaumicRecords.createTranslationKey("research_desc", "enchant"),
                    CAT_BASIC, AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/enchant.png"), null, new ResourceLocation[0], -4, -2, 0,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> KNOWFRAG = REGISTRAR.register("knowfrag",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "knowfrag"), ThaumicRecords.createTranslationKey("research_desc", "knowfrag"),
                    CAT_BASIC, AspectList.empty(), null, new ItemStack(ItemRegistry.KNOWLEDGE_FRAGMENT.get()), new ResourceLocation[]{RESEARCH.getId()}, 3, -2,
                    0, Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> THAUMONOMICON = REGISTRAR.register("thaumonomicon",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "thaumonomicon"),
                    ThaumicRecords.createTranslationKey("research_desc", "thaumonomicon"), CAT_BASIC, AspectList.empty(), null,
                    new ItemStack(ItemRegistry.THAUMONOMICON.get()), new ResourceLocation[]{RESEARCH.getId()}, 1, -2, 0, Research.RenderStrategy.ROUND,
                    Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    public static final DeferredHolder<Research, Research> NODETAPPER1 = REGISTRAR.register("nodetapper1",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "nodetapper1"),
                    ThaumicRecords.createTranslationKey("research_desc", "nodetapper1"), CAT_BASIC, new AspectList().add(AspectRegistry.AURAM.getId(), 3).add(
                    AspectRegistry.PRAECANTATIO.getId(), 3).add(AspectRegistry.MOTUS.getId(), 3).add(AspectRegistry.PERMUTATIO.getId(), 3),
                    ThaumicRecords.createRl("textures/research/icon/nodetapper1.png"), null, new ResourceLocation[]{NODES.getId()}, -4, 1, 2,
                    Research.RenderStrategy.ROUND, Research.UnlockStrategy.RESEARCH, List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> RESEARCHER1 = REGISTRAR.register("researcher1",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "researcher1"),
                    ThaumicRecords.createTranslationKey("research_desc", "researcher1"), CAT_BASIC, new AspectList().add(AspectRegistry.COGNITIO.getId(), 3)
                    .add(AspectRegistry.SENSUS.getId(), 3)
                    .add(AspectRegistry.ORDO.getId(), 3), ThaumicRecords.createRl("textures/research/icon/researcher1.png"), null,
                    new ResourceLocation[]{RESEARCH.getId()}, 4, 1, 1, Research.RenderStrategy.ROUND, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> NODEPRESERVE = REGISTRAR.register("nodepreserve",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "nodepreserve"),
                    ThaumicRecords.createTranslationKey("research_desc", "nodepreserve"), CAT_BASIC, new AspectList().add(AspectRegistry.AURAM.getId(), 3)
                    .add(AspectRegistry.LUCRUM.getId(), 3)
                    .add(AspectRegistry.SENSUS.getId(), 3), ThaumicRecords.createRl("textures/research/icon/nodepreserve.png"), null,
                    new ResourceLocation[]{NODETAPPER1.getId()}, -6, 2, 2, Research.RenderStrategy.ROUND, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> NODETAPPER2 = REGISTRAR.register("nodetapper2",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "nodetapper2"),
                    ThaumicRecords.createTranslationKey("research_desc", "nodetapper2"), CAT_BASIC, new AspectList().add(AspectRegistry.AURAM.getId(), 6).add(
                    AspectRegistry.PRAECANTATIO.getId(), 3).add(AspectRegistry.MOTUS.getId(), 3).add(AspectRegistry.PERMUTATIO.getId(), 3),
                    ThaumicRecords.createRl("textures/research/icon/nodetapper2.png"), null, new ResourceLocation[]{NODETAPPER1.getId()}, -3, 3, 2,
                    Research.RenderStrategy.SPIKY, Research.UnlockStrategy.RESEARCH, List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> DECONSTRUCTOR = REGISTRAR.register("deconstructor",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "deconstructor"),
                    ThaumicRecords.createTranslationKey("research_desc", "deconstructor"), CAT_BASIC, new AspectList().add(AspectRegistry.COGNITIO.getId(), 3)
                    .add(AspectRegistry.FABRICO.getId(), 3)
                    .add(AspectRegistry.PERDITIO.getId(), 3), null, new ItemStack(ItemRegistry.DECONSTRUCTION_TABLE.get()),
                    new ResourceLocation[]{RESEARCHER1.getId()}, 6, 2, 1, Research.RenderStrategy.ROUND, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> RESEARCHER2 = REGISTRAR.register("researcher2",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "researcher2"),
                    ThaumicRecords.createTranslationKey("research_desc", "researcher2"), CAT_BASIC, new AspectList().add(AspectRegistry.COGNITIO.getId(), 6)
                    .add(AspectRegistry.ORDO.getId(), 3)
                    .add(AspectRegistry.SENSUS.getId(), 3)
                    .add(AspectRegistry.PRAECANTATIO.getId(), 3), ThaumicRecords.createRl("textures/research/icon/researcher2.png"), null,
                    new ResourceLocation[]{RESEARCHER1.getId()}, 3, 3, 2, Research.RenderStrategy.SPIKY, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 1));

    public static final DeferredHolder<Research, Research> NODEJAR = REGISTRAR.register("nodejar",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "nodejar"), ThaumicRecords.createTranslationKey("research_desc", "nodejar"),
                    CAT_BASIC, new AspectList().add(AspectRegistry.AURAM.getId(), 6)
                    .add(AspectRegistry.LUCRUM.getId(), 3)
                    .add(AspectRegistry.PERMUTATIO.getId(), 3)
                    .add(AspectRegistry.MOTUS.getId(), 3), null, new ItemStack(ItemRegistry.JAR.get())/*TODO replace with nodejar*/,
                    new ResourceLocation[]{NODEPRESERVE.getId()}, -7, 4, 3, Research.RenderStrategy.NORMAL, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> RESEARCHDUPE = REGISTRAR.register("researchdupe",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "researchdupe"),
                    ThaumicRecords.createTranslationKey("research_desc", "researchdupe"), CAT_BASIC, new AspectList().add(AspectRegistry.COGNITIO.getId(), 6)
                    .add(AspectRegistry.PERMUTATIO.getId(), 3)
                    .add(AspectRegistry.SENSUS.getId(), 3)
                    .add(AspectRegistry.LUCRUM.getId(), 3)
                    .add(AspectRegistry.FABRICO.getId(), 3), ThaumicRecords.createRl("textures/research/icon/researchdupe.png"), null,
                    new ResourceLocation[]{RESEARCHER2.getId()}, 4, 5, 3, Research.RenderStrategy.ROUND, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.PARENT), 0));

    public static final DeferredHolder<Research, Research> CRIMSON = REGISTRAR.register("crimson",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "crimson"), ThaumicRecords.createTranslationKey("research_desc", "crimson"),
                    CAT_BASIC, AspectList.empty(), null, new ItemStack(ItemRegistry.CRIMSON_RITES.get()), new ResourceLocation[0], 0, 4, 0,
                    Research.RenderStrategy.SPIKY, Research.UnlockStrategy.RESEARCH,
                    List.of(Research.DiscoveryStrategy.SCAN, Research.DiscoveryStrategy.FRAGMENT), 3));

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCHES));
    }

}
