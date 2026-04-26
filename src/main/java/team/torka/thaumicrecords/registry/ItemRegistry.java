package team.torka.thaumicrecords.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.item.GogglesItem;
import team.torka.thaumicrecords.item.PrimordialPearItem;
import team.torka.thaumicrecords.item.WandItem;
import team.torka.thaumicrecords.item.WispEssenceItem;

public class ItemRegistry {
    public static final DeferredRegister.Items REGISTRAR = DeferredRegister.createItems(ThaumicRecords.MOD_ID);

    // Simple Items
    // @formatter:off
    public static final DeferredItem<Item> AMBER = REGISTRAR.registerSimpleItem("amber");
    public static final DeferredItem<Item> BATH_SALTS = REGISTRAR.registerSimpleItem("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = REGISTRAR.registerSimpleItem("primal_charm");
    public static final DeferredItem<Item> ENCHANTED_FABRIC = REGISTRAR.registerSimpleItem("enchanted_fabric");
    public static final DeferredItem<Item> COIN = REGISTRAR.registerSimpleItem("coin");
    public static final DeferredItem<Item> SALIS_MUNDUS = REGISTRAR.registerSimpleItem("salis_mundus");

    public static final DeferredItem<Item> PRIMORDIAL_PEARL=REGISTRAR.registerItem("primordial_pearl", PrimordialPearItem::new, itemProp(1));


    public static final DeferredItem<Item> WAND_CAP_IRON = REGISTRAR.registerSimpleItem("wand_cap_iron");
    public static final DeferredItem<Item> WAND_CAP_GOLD = REGISTRAR.registerSimpleItem("wand_cap_gold");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM = REGISTRAR.registerSimpleItem("wand_cap_thaumium");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM_INERT = REGISTRAR.registerSimpleItem("wand_cap_thaumium_inert");
    public static final DeferredItem<Item> WAND_ROD_GREATWOOD = REGISTRAR.registerSimpleItem("wand_rod_greatwood");
    public static final DeferredItem<Item> WAND_ROD_SILVERWOOD = REGISTRAR.registerSimpleItem("wand_rod_silverwood");

    public static final DeferredItem<WandItem> WAND = REGISTRAR.registerItem("wand", WandItem::new, itemProp(1));

    public static final DeferredItem<GogglesItem> GOGGLES=REGISTRAR.register("goggles",GogglesItem::new);

    public static final DeferredItem<WispEssenceItem> WISP_ESSENCE=REGISTRAR.register("wisp_essence",WispEssenceItem::new);

    // Block Items
    public static final DeferredItem<BlockItem> AURA_NODE = REGISTRAR.register("aura_node",
            () -> new BlockItem(BlockRegistry.AURA_NODE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_WORKBENCH = REGISTRAR.register("arcane_workbench",
            () -> new BlockItem(BlockRegistry.ARCANE_WORKBENCH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TABLE = REGISTRAR.register("table",
            () -> new BlockItem(BlockRegistry.TABLE.get(), new Item.Properties()));
    // @formatter:on
    public static void putInCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(ironCappedWoodWand());
        output.accept(goldCappedGreatwoodWand());
        output.accept(thaumiumCappedSilverwoodWand());
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
        output.accept(GOGGLES);
        output.accept(TABLE);
        output.accept(ARCANE_WORKBENCH);
    }

    public static void putInWipCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(PRIMORDIAL_PEARL);
        output.accept(AURA_NODE);
    }

    public static void putInWispEssenceCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        AspectRegistry.ASPECT_REGISTRY.forEach(aspect -> {
            ItemStack stack = new ItemStack(ItemRegistry.WISP_ESSENCE.get());
            AspectList list = new AspectList();
            list.put(AspectRegistry.ASPECT_REGISTRY.getKey(aspect), 2);
            stack.set(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(list));
            output.accept(stack);
        });
    }

    private static ItemStack ironCappedWoodWand() {
        ItemStack initialWand = new ItemStack(WAND.get());
        AspectList initialAspects = new AspectList();
        WandRod wandRod = WandRodRegistry.WAND_ROD_WOOD.get();
        for (Aspect aspect : Aspect.getPrimal()) {
            initialAspects.put(ThaumicRecords.createRl(aspect.getName()), wandRod.getCapacity());
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), initialAspects);
        initialWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return initialWand;
    }

    private static ItemStack goldCappedGreatwoodWand() {
        ItemStack initialWand = new ItemStack(WAND.get());
        AspectList initialAspects = new AspectList();
        WandRod wandRod = WandRodRegistry.WAND_ROD_GREATWOOD.get();
        for (Aspect aspect : Aspect.getPrimal()) {
            initialAspects.put(ThaumicRecords.createRl(aspect.getName()), wandRod.getCapacity());
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_GREATWOOD.getId(), WandCapRegistry.WAND_CAP_GOLD.getId(),
                initialAspects);
        initialWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return initialWand;
    }

    private static ItemStack thaumiumCappedSilverwoodWand() {
        ItemStack initialWand = new ItemStack(WAND.get());
        AspectList initialAspects = new AspectList();
        WandRod wandRod = WandRodRegistry.WAND_ROD_SILVERWOOD.get();
        for (Aspect aspect : Aspect.getPrimal()) {
            initialAspects.put(ThaumicRecords.createRl(aspect.getName()), wandRod.getCapacity());
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_SILVERWOOD.getId(), WandCapRegistry.WAND_CAP_THAUMIUM.getId(),
                initialAspects);
        initialWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return initialWand;
    }

    private static Item.Properties itemProp() {
        return new Item.Properties();
    }

    private static Item.Properties itemProp(int stackSize) {
        return new Item.Properties().stacksTo(stackSize);
    }
}