package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import team.torka.thaumicrecords.client.screen.ArcaneWorkbenchScreen;
import team.torka.thaumicrecords.registry.MenuRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterMenuScreensEventListener {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
    }
}
