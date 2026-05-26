package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import team.torka.thaumicrecords.ThaumicRecords;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModelEventsListener {
    @SubscribeEvent
    public static void onRegisterAdditionalEvent(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(ThaumicRecords.createRl("item/thaumometer_obj")));
        event.register(ModelResourceLocation.standalone(ThaumicRecords.createRl("block/thaumatorium_obj")));
    }
}
