package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import team.torka.thaumicrecords.client.model.ArcaneWorkbenchModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterLayerDefinitionsListener {

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CustomModelLayer.ARCANE_WORKTABLE, ArcaneWorkbenchModel::createLayerDefinition);
    }
}
