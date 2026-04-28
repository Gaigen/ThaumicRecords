package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import team.torka.thaumicrecords.client.renderer.blockentity.ArcaneWorkbenchRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.AuraNodeRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.ResearchTableBlockEntityRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.TableRenderer;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityRenderersEventListener {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.AURA_NODE.get(), AuraNodeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.TABLE.get(), TableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.RESEARCH_TABLE.get(), ResearchTableBlockEntityRenderer::new);
    }
}
