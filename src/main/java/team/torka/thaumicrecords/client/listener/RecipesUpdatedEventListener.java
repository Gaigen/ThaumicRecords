package team.torka.thaumicrecords.client.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import team.torka.thaumicrecords.api.helper.AspectHelper;

@EventBusSubscriber(value = Dist.CLIENT)
public class RecipesUpdatedEventListener {

    @SubscribeEvent
    public static void onEvent(RecipesUpdatedEvent event) {
        AspectHelper.rebuildAspectRegistrationRecipeCache(event.getRecipeManager());
    }
}
