package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import team.torka.thaumicrecords.client.model.ArcaneWorkbenchModel;
import team.torka.thaumicrecords.client.model.ResearchTableModel;
import team.torka.thaumicrecords.client.model.TableModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;
import team.torka.thaumicrecords.client.renderer.blockentity.ArcanePedestalRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.ArcaneWorkbenchRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.AuraNodeRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.CrucibleRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.ResearchTableRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.TableRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.ThaumatoriumRenderer;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class EntityRenderersEventListener {
    @SubscribeEvent
    public static void onRegisterRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.AURA_NODE.get(), AuraNodeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.TABLE.get(), TableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.RESEARCH_TABLE.get(), ResearchTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ARCANE_PEDESTAL.get(), ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.CRUCIBLE.get(), CrucibleRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.THAUMATORIUM.get(), ThaumatoriumRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitionsEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CustomModelLayer.ARCANE_WORKBENCH, ArcaneWorkbenchModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.TABLE, TableModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.RESEARCH_TABLE, ResearchTableModel::createLayerDefinition);
    }
}
