package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import team.torka.thaumicrecords.client.tooltip.AspectTooltipComponent;
import team.torka.thaumicrecords.client.tooltip.ClientAspectTooltipComponent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterClientTooltipComponentFactoriesEventListener {

    @SubscribeEvent
    public static void onEvent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AspectTooltipComponent.class, ClientAspectTooltipComponent::new);
    }
}
