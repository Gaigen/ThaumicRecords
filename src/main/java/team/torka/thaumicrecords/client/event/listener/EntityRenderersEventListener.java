package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import team.torka.thaumicrecords.client.model.ArcaneWorkbenchModel;
import team.torka.thaumicrecords.client.model.JarModel;
import team.torka.thaumicrecords.client.model.FortressArmorModel;
import team.torka.thaumicrecords.client.model.KnightArmorModel;
import team.torka.thaumicrecords.client.model.LeaderArmorModel;
import team.torka.thaumicrecords.client.model.ResearchTableModel;
import team.torka.thaumicrecords.client.model.RobeArmorModel;
import team.torka.thaumicrecords.client.model.TableModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;
import team.torka.thaumicrecords.client.renderer.blockentity.*;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;

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
		    event.registerBlockEntityRenderer(BlockEntityRegistry.JAR.get(), JarBlockEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FOLLOWING_ITEM.get(), ItemEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitionsEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CustomModelLayer.ARCANE_WORKBENCH, ArcaneWorkbenchModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.TABLE, TableModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.RESEARCH_TABLE, ResearchTableModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.JAR, JarModel::createLayerDefinition);
        event.registerLayerDefinition(CustomModelLayer.FORTRESS_ARMOR, FortressArmorModel::createBodyLayer);
        event.registerLayerDefinition(CustomModelLayer.KNIGHT_ARMOR, KnightArmorModel::createBodyLayer);
        event.registerLayerDefinition(CustomModelLayer.ROBE_ARMOR, RobeArmorModel::createBodyLayer);
        event.registerLayerDefinition(CustomModelLayer.LEADER_ARMOR, LeaderArmorModel::createBodyLayer);
    }
}
