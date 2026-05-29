package team.torka.thaumicrecords.integration.curios;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.ItemRegistry;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber
public class CuriosIntegration {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Mundane baubles
        CuriosApi.registerCurio(ItemRegistry.MUNDANE_RING.get(), ItemRegistry.MUNDANE_RING.get());
        CuriosApi.registerCurio(ItemRegistry.MUNDANE_AMULET.get(), ItemRegistry.MUNDANE_AMULET.get());
        CuriosApi.registerCurio(ItemRegistry.MUNDANE_BELT.get(), ItemRegistry.MUNDANE_BELT.get());

        // Runic rings
        CuriosApi.registerCurio(ItemRegistry.RUNIC_RING_LESSER.get(), ItemRegistry.RUNIC_RING_LESSER.get());
        CuriosApi.registerCurio(ItemRegistry.RUNIC_RING.get(), ItemRegistry.RUNIC_RING.get());
        CuriosApi.registerCurio(ItemRegistry.RUNIC_RING_CHARGED.get(), ItemRegistry.RUNIC_RING_CHARGED.get());
        CuriosApi.registerCurio(ItemRegistry.RUNIC_RING_REGEN.get(), ItemRegistry.RUNIC_RING_REGEN.get());

        // Runic amulets
        CuriosApi.registerCurio(ItemRegistry.RUNIC_AMULET.get(), ItemRegistry.RUNIC_AMULET.get());
        CuriosApi.registerCurio(ItemRegistry.RUNIC_AMULET_EMERGENCY.get(), ItemRegistry.RUNIC_AMULET_EMERGENCY.get());

        // Runic girdles
        CuriosApi.registerCurio(ItemRegistry.RUNIC_GIRDLE.get(), ItemRegistry.RUNIC_GIRDLE.get());
        CuriosApi.registerCurio(ItemRegistry.RUNIC_GIRDLE_KINETIC.get(), ItemRegistry.RUNIC_GIRDLE_KINETIC.get());

        ThaumicRecords.LOGGER.info("Curios capabilities registered");
    }
}
