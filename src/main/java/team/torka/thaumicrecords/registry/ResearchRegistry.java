package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

@EventBusSubscriber
public class ResearchRegistry {
    public static final DeferredRegister<Research> REGISTRAR = DeferredRegister.create(RegistryKeys.RESEARCHES, ThaumicRecords.MOD_ID);
    public static Registry<Research> RESEARCH_REGISTRY = null;

    public static final DeferredHolder<Research, Research> TEST = REGISTRAR.register("test",
            () -> Research.createNormal("test", "testdesc", ResearchCategoryRegistry.TEST.getId(), AspectList.empty(), null, new ItemStack(Items.NAME_TAG),
                    null, 10, 10, 3));

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        RESEARCH_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.RESEARCHES));
    }

}
