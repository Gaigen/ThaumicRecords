package team.torka.thaumicrecords.register;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

public class TRItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ThaumicRecords.MOD_ID);

    // Simple Items
    // @formatter:off
    public static final DeferredItem<Item> AMBER = REGISTER.registerSimpleItem("amber");
    public static final DeferredItem<Item> BATH_SALTS = REGISTER.registerSimpleItem("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = REGISTER.registerSimpleItem("primal_charm");
    public static final DeferredItem<Item> ENCHANTED_FABRIC = REGISTER.registerSimpleItem("enchanted_fabric");
    public static final DeferredItem<Item> COIN = REGISTER.registerSimpleItem("coin");
    public static final DeferredItem<Item> SALIS_MUNDUS = REGISTER.registerSimpleItem("salis_mundus");
    // @formatter:on

    // Advanced Items


    // Block Items

    public static void putInCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(AMBER);
        output.accept(BATH_SALTS);
        output.accept(PRIMAL_CHARM);
        output.accept(ENCHANTED_FABRIC);
        output.accept(COIN);
        output.accept(SALIS_MUNDUS);
    }

    public static void putInWipCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {

    }
}
