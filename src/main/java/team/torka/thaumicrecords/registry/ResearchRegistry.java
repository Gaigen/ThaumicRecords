package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.research.Research;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class ResearchRegistry {
    public static final DeferredRegister<Research> REGISTRAR = DeferredRegister.create(RegistryKeys.RESEARCHES, ThaumicRecords.MOD_ID);
    public static Registry<Research> RESEARCH_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCHES));
    }

}
