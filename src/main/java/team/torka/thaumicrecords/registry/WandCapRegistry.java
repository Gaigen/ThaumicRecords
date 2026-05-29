package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.item.WandCap;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class WandCapRegistry {
    public static final DeferredRegister<WandCap> REGISTRAR = DeferredRegister.create(RegistryKeys.WAND_CAPS, ThaumicRecords.MOD_ID);

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
            REGISTRAR.register("thaumium", () -> new WandCap("thaumium", WandCap.getAllAspectModifierWithAmount(0.9), 6,ItemRegistry.WAND_CAP_THAUMIUM.get()));

    // Copper: 1.1 base, 1.0 for ordo+perditio
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_COPPER =
            REGISTRAR.register("copper", () -> {
                Map<ResourceLocation, Double> mods = new HashMap<>(WandCap.getAllAspectModifierWithAmount(1.1));
                mods.put(AspectRegistry.ORDO.getId(), 1.0);
                mods.put(AspectRegistry.PERDITIO.getId(), 1.0);
                return new WandCap("copper", mods, 2, ItemRegistry.WAND_CAP_COPPER.get());
            });

    // Silver: 1.0 base, 0.95 for primal elements (aer, ignis, aqua, terra)
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_SILVER =
            REGISTRAR.register("silver", () -> {
                Map<ResourceLocation, Double> mods = new HashMap<>(WandCap.getAllAspectModifierWithAmount(1.0));
                mods.put(AspectRegistry.AER.getId(), 0.95);
                mods.put(AspectRegistry.IGNIS.getId(), 0.95);
                mods.put(AspectRegistry.AQUA.getId(), 0.95);
                mods.put(AspectRegistry.TERRA.getId(), 0.95);
                return new WandCap("silver", mods, 4, ItemRegistry.WAND_CAP_SILVER.get());
            });

    // Void: 0.8 for all
    public static final DeferredHolder<WandCap, WandCap> WAND_CAP_VOID =
            REGISTRAR.register("void", () -> new WandCap("void", WandCap.getAllAspectModifierWithAmount(0.8), 9, ItemRegistry.WAND_CAP_VOID.get()));

    /*@formatter:on*/
}
