package team.torka.thaumicrecords.event.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import team.torka.thaumicrecords.data.manager.AspectRegistrationManager;

@EventBusSubscriber
public class AddReloadListenerEventListener {
    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new AspectRegistrationManager());
    }
}
