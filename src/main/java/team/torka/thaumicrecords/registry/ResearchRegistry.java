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
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.research.Research;

import java.util.List;

@EventBusSubscriber
public class ResearchRegistry {
    public static final DeferredRegister<Research> REGISTRAR = DeferredRegister.create(RegistryKeys.RESEARCHES, ThaumicRecords.MOD_ID);
    public static Registry<Research> RESEARCH_REGISTRY = null;

    public static final DeferredHolder<Research, Research> ASPECTS = REGISTRAR.register("aspects",
            () -> new Research(ThaumicRecords.createTranslationKey("research", "aspects"), ThaumicRecords.createTranslationKey("research_desc", "aspects"),
                    ResearchCategoryRegistry.BASIC.getId(), AspectList.empty(), ThaumicRecords.createRl("textures/research/icon/aspects.png"), null, null, 0, 0,
                    0, Research.RenderStrategy.ROUND, Research.UnlockStrategy.INITIAL, List.of(Research.DiscoveryStrategy.ALWAYS), 0));

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCHES));
    }

}
