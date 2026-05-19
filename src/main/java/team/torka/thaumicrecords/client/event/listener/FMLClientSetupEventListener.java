package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT)
public class FMLClientSetupEventListener {
    @SubscribeEvent
    public static void onEvent(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> ItemProperties.register(ItemRegistry.RESEARCH_NOTES.get(), ThaumicRecords.createRl("completed"), (stack, level, entity, seed) -> {
                    ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
                    return (Objects.nonNull(data) && data.complete()) ? 1.0F : 0.0F;
                }));
        event.enqueueWork(() -> {
            FireBlock fireBlock = (FireBlock) Blocks.FIRE;
            fireBlock.setFlammable(BlockRegistry.SILVERWOOD_LOG.get(), 5, 5);
            fireBlock.setFlammable(BlockRegistry.SILVERWOOD_LEAVES.get(), 30, 60);
            fireBlock.setFlammable(BlockRegistry.GREATWOOD_LOG.get(), 5, 5);
            fireBlock.setFlammable(BlockRegistry.GREATWOOD_LEAVES.get(), 30, 60);
        });
    }
}
