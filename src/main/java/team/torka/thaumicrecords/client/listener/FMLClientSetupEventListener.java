package team.torka.thaumicrecords.client.listener;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class FMLClientSetupEventListener {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ItemRegistry.RESEARCH_NOTES.get(), ResourceLocation.fromNamespaceAndPath("thaumicrecords", "completed"),
                (stack, level, entity, seed) -> {
                    ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
                    return (data != null && data.complete()) ? 1.0F : 0.0F;
                }));
    }
}
