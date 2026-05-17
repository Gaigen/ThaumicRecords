package team.torka.thaumicrecords.event.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

@EventBusSubscriber
public class RegisterCapabilitiesEventListener {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ARCANE_PEDESTAL.get(),
                (blockEntity, direction) -> blockEntity.getItemStackHandler());
    }
}
