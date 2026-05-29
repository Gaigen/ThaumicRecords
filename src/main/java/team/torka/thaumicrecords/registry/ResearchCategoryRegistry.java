package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
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

    public static final DeferredHolder<ResearchCategory, ResearchCategory> TEST = REGISTRAR.register("test",
            () -> new ResearchCategory("test", null, new ItemStack(ItemRegistry.WAND.get(), 1),
                    ThaumicRecords.createRl("textures/research/background/researchback.png")));

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCH_CATEGORIES));
    }

}
