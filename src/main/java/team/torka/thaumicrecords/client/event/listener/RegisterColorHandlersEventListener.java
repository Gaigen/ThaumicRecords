package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.item.PhialItem;
import team.torka.thaumicrecords.item.WispEssenceItem;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterColorHandlersEventListener {

    @SubscribeEvent
    public static void onItemEvent(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }
            WispEssenceItem item = (WispEssenceItem) stack.getItem();
            AspectList aspects = item.getAspects(stack);
            if (!aspects.isEmpty()) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(aspects.firstEntry().getKey());
                if (Objects.isNull(aspect)) {
                    return -1;
                }
                return aspect.getARGBColor();
            } else {
                // Wisp Essence blinking for creative tab
                int registrySize = AspectRegistry.ASPECT_REGISTRY.size();
                if (registrySize == 0) {
                    return -1;
                }
                int index = (int) (System.currentTimeMillis() / 500L % (long) registrySize);
                return AspectRegistry.ASPECT_REGISTRY.stream().skip(index).findFirst().map(Aspect::getARGBColor).orElse(-1);
            }
        }, ItemRegistry.WISP_ESSENCE.get());

        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) {
                return -1;
            }
            PhialItem item = (PhialItem) stack.getItem();
            AspectList aspects = item.getAspects(stack);
            if (!aspects.isEmpty()) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(aspects.firstEntry().getKey());
                if (Objects.isNull(aspect)) {
                    return -1;
                }
                return aspect.getARGBColor();
            }
            return -1;
        }, ItemRegistry.PHIAL.get());

        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
                return Objects.nonNull(data) ? data.color() | 0xFF000000 : -1;
            }
            return -1;
        }, ItemRegistry.RESEARCH_NOTES.get());

        event.register((stack, tintIndex) -> 16777086 | 0xFF000000, ItemRegistry.AER_SHARD.get());
        event.register((stack, tintIndex) -> 16727041 | 0xFF000000, ItemRegistry.IGNIS_SHARD.get());
        event.register((stack, tintIndex) -> 37119 | 0xFF000000, ItemRegistry.AQUA_SHARD.get());
        event.register((stack, tintIndex) -> 40960 | 0xFF000000, ItemRegistry.TERRA_SHARD.get());
        event.register((stack, tintIndex) -> 15650047 | 0xFF000000, ItemRegistry.ORDO_SHARD.get());
        event.register((stack, tintIndex) -> 5592439 | 0xFF000000, ItemRegistry.PERDITIO_SHARD.get());
        event.register((stack, tintIndex) -> FoliageColor.get(0.5D, 1.0D), BlockRegistry.GREATWOOD_LEAVES.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 16777086 | 0xFF000000 : -1, BlockRegistry.AER_INFUSED_STONE.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 16727041 | 0xFF000000 : -1, BlockRegistry.IGNIS_INFUSED_STONE.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 37119 | 0xFF000000 : -1, BlockRegistry.AQUA_INFUSED_STONE.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 40960 | 0xFF000000 : -1, BlockRegistry.TERRA_INFUSED_STONE.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 15650047 | 0xFF000000 : -1, BlockRegistry.ORDO_INFUSED_STONE.get());
        event.register((stack, tintIndex) -> tintIndex == 1 ? 5592439 | 0xFF000000 : -1, BlockRegistry.PERDITIO_INFUSED_STONE.get());
    }

    @SubscribeEvent
    public static void onBlockEvent(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (Objects.nonNull(level) && Objects.nonNull(pos)) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }
            return FoliageColor.getDefaultColor();
        }, BlockRegistry.GREATWOOD_LEAVES.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 16777086 | 0xFF000000 : -1, BlockRegistry.AER_INFUSED_STONE.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 16727041 | 0xFF000000 : -1, BlockRegistry.IGNIS_INFUSED_STONE.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 37119 | 0xFF000000 : -1, BlockRegistry.AQUA_INFUSED_STONE.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 40960 | 0xFF000000 : -1, BlockRegistry.TERRA_INFUSED_STONE.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 15650047 | 0xFF000000 : -1, BlockRegistry.ORDO_INFUSED_STONE.get());
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 5592439 | 0xFF000000 : -1, BlockRegistry.PERDITIO_INFUSED_STONE.get());
    }
}
