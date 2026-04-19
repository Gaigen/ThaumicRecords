package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

public class CreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> REGISTRAR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = REGISTRAR.register("tab",
            () -> CreativeModeTab.builder()
                                 .title(Component.translatable("itemGroup." + ThaumicRecords.MOD_ID + ".tab"))
                                 .withTabsBefore(CreativeModeTabs.COMBAT)
                                 .icon(() -> new ItemStack(ItemRegistry.SALIS_MUNDUS.get()))
                                 .displayItems(ItemRegistry::putInCreativeTab)
                                 .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WIP = REGISTRAR.register("wip_tab",
            () -> CreativeModeTab.builder()
                                 .title(Component.translatable("itemGroup." + ThaumicRecords.MOD_ID + ".wip_tab"))
                                 .withTabsBefore(CreativeModeTabs.COMBAT)
                                 .icon(() -> new ItemStack(ItemRegistry.PRIMAL_CHARM.get()))
                                 .displayItems(ItemRegistry::putInWipCreativeTab)
                                 .build());
}
