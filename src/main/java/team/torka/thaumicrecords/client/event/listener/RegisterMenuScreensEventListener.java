package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import team.torka.thaumicrecords.client.screen.ArcaneWorkbenchScreen;
import team.torka.thaumicrecords.client.screen.DeconstructionTableScreen;
import team.torka.thaumicrecords.client.screen.ResearchTableScreen;
import team.torka.thaumicrecords.client.screen.ThaumatoriumScreen;
import team.torka.thaumicrecords.registry.MenuRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterMenuScreensEventListener {

    @SubscribeEvent
    public static void onEvent(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
        event.register(MenuRegistry.RESEARCH_TABLE.get(), ResearchTableScreen::new);
        event.register(MenuRegistry.THAUMATORIUM.get(), ThaumatoriumScreen::new);
        event.register(MenuRegistry.DECONSTRUCTION_TABLE.get(), DeconstructionTableScreen::new);
    }
}
