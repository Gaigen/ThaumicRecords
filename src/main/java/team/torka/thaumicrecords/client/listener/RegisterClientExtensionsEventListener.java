package team.torka.thaumicrecords.client.listener;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import team.torka.thaumicrecords.client.renderer.WandRenderer;
import team.torka.thaumicrecords.registry.ItemRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterClientExtensionsEventListener {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return WandRenderer.INSTANCE;
            }
        }, ItemRegistry.WAND.get());
    }
}
