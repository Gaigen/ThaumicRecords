package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.item.WandCap;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class WandCapRegistry {
    public static final DeferredRegister<WandCap> REGISTRAR =
            DeferredRegister.create(RegistryKeys.WAND_CAPS, ThaumicRecords.MOD_ID);

    public static Registry<WandCap> WAND_CAP_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        WAND_CAP_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.WAND_CAPS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_IRON =
            REGISTRAR.register("iron", () -> new WandCap("iron", WandCap.getAllAspectModifierWithAmount(1.1), 1,ItemRegistry.WAND_CAP_IRON.get()));
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_GOLD =
            REGISTRAR.register("gold", () -> new WandCap("gold", WandCap.getAllAspectModifierWithAmount(1.0), 3,ItemRegistry.WAND_CAP_GOLD.get()));
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_THAUMIUM =
            REGISTRAR.register("thaumium", () -> new WandCap("gold", WandCap.getAllAspectModifierWithAmount(0.9), 6,ItemRegistry.WAND_CAP_THAUMIUM.get()));

    /*@formatter:on*/
}
