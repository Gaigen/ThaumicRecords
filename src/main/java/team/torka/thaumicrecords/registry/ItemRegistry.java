package team.torka.thaumicrecords.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.item.PrimordialPearl;
import team.torka.thaumicrecords.item.Wand;

public class ItemRegistry {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ThaumicRecords.MOD_ID);

    // Simple Items
    // @formatter:off
    public static final DeferredItem<Item> AMBER = REGISTER.registerSimpleItem("amber");
    public static final DeferredItem<Item> BATH_SALTS = REGISTER.registerSimpleItem("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = REGISTER.registerSimpleItem("primal_charm");
    public static final DeferredItem<Item> ENCHANTED_FABRIC = REGISTER.registerSimpleItem("enchanted_fabric");
    public static final DeferredItem<Item> COIN = REGISTER.registerSimpleItem("coin");
    public static final DeferredItem<Item> SALIS_MUNDUS = REGISTER.registerSimpleItem("salis_mundus");

    public static final DeferredItem<Item> PRIMORDIAL_PEARL=REGISTER.registerItem("primordial_pearl", PrimordialPearl::new, itemProp(1));


    public static final DeferredItem<Item> WAND_CAP_IRON = REGISTER.registerSimpleItem("wand_cap_iron");
    public static final DeferredItem<Item> WAND_CAP_GOLD = REGISTER.registerSimpleItem("wand_cap_gold");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM = REGISTER.registerSimpleItem("wand_cap_thaumium");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM_INERT = REGISTER.registerSimpleItem("wand_cap_thaumium_inert");
    public static final DeferredItem<Item> WAND_ROD_GREATWOOD = REGISTER.registerSimpleItem("wand_rod_greatwood");
    public static final DeferredItem<Item> WAND_ROD_SILVERWOOD = REGISTER.registerSimpleItem("wand_rod_silverwood");

    public static final DeferredItem<Wand> WAND = REGISTER.registerItem("wand", Wand::new, itemProp(1));

    // Block Items
    // @formatter:on
    public static void putInCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(AMBER);
        output.accept(BATH_SALTS);
        output.accept(PRIMAL_CHARM);
        output.accept(ENCHANTED_FABRIC);
        output.accept(COIN);
        output.accept(SALIS_MUNDUS);
        output.accept(WAND_CAP_IRON);
        output.accept(WAND_CAP_GOLD);
        output.accept(WAND_CAP_THAUMIUM);
        output.accept(WAND_CAP_THAUMIUM_INERT);
        output.accept(WAND_ROD_GREATWOOD);
        output.accept(WAND_ROD_SILVERWOOD);
    }

    public static void putInWipCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(WAND);
        output.accept(PRIMORDIAL_PEARL);
    }

    private static Item.Properties itemProp() {
        return new Item.Properties();
    }

    private static Item.Properties itemProp(int stackSize) {
        return new Item.Properties().stacksTo(stackSize);
    }
}