//package team.torka.thaumicrecords.client.event;
//
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.client.event.ModelEvent;
//import team.torka.thaumicrecords.client.model.AuraNodeBakedModel;
//import team.torka.thaumicrecords.common.register.TRItems;
//
//import java.util.Map;
//
//@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//public class ClientEventHandler {
//    @SubscribeEvent
//    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
//        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
//        ModelResourceLocation location =
//                new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(TRItems.AURA_NODE.get()), "inventory");
//        BakedModel existingModel = modelRegistry.get(location);
//        if (existingModel == null) {
//            throw new RuntimeException("Did not find Obsidian Hidden in registry");
//        } else if (existingModel instanceof AuraNodeBakedModel) {
//            throw new RuntimeException("Tried to replaceObsidian Hidden twice");
//        } else {
//            AuraNodeBakedModel auraNodeBakedModel = new AuraNodeBakedModel(existingModel);
//            event.getModels().put(location, auraNodeBakedModel);
//        }
//    }
//}
