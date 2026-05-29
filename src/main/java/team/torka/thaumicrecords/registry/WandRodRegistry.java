package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.item.WandRod;

@EventBusSubscriber
public class WandRodRegistry {
    public static final DeferredRegister<WandRod> REGISTRAR = DeferredRegister.create(RegistryKeys.WAND_RODS, ThaumicRecords.MOD_ID);

    public static Registry<WandRod> WAND_ROD_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        WAND_ROD_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.WAND_RODS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_WOOD =
            REGISTRAR.register("wood", () -> new WandRod("wood", 2500, 1, Items.STICK));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_GREATWOOD =
            REGISTRAR.register("greatwood", () -> new WandRod("greatwood", 5000, 3, ItemRegistry.WAND_ROD_GREATWOOD.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_SILVERWOOD =
            REGISTRAR.register("silverwood", () -> new WandRod("silverwood", 10000, 9, ItemRegistry.WAND_ROD_SILVERWOOD.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_OBSIDIAN =
            REGISTRAR.register("obsidian", () -> new WandRod("obsidian", 7500, 6, ItemRegistry.WAND_ROD_OBSIDIAN.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_BLAZE =
            REGISTRAR.register("blaze", () -> new WandRod("blaze", 7500, 6, ItemRegistry.WAND_ROD_BLAZE.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_ICE =
            REGISTRAR.register("ice", () -> new WandRod("ice", 7500, 6, ItemRegistry.WAND_ROD_ICE.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_QUARTZ =
            REGISTRAR.register("quartz", () -> new WandRod("quartz", 7500, 6, ItemRegistry.WAND_ROD_QUARTZ.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_BONE =
            REGISTRAR.register("bone", () -> new WandRod("bone", 7500, 6, ItemRegistry.WAND_ROD_BONE.get()));
    public static final DeferredHolder<WandRod, WandRod> WAND_ROD_REED =
            REGISTRAR.register("reed", () -> new WandRod("reed", 7500, 6, ItemRegistry.WAND_ROD_REED.get()));
    /*@formatter:on*/
}
