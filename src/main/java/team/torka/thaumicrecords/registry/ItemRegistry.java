package team.torka.thaumicrecords.registry;

import net.minecraft.resources.ResourceLocation;
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
import team.torka.thaumicrecords.item.ResearchNotesItem;
import team.torka.thaumicrecords.item.ScribingToolsItem;
import team.torka.thaumicrecords.item.ThaumometerItem;
import team.torka.thaumicrecords.item.WandItem;
import team.torka.thaumicrecords.item.WispEssenceItem;

public class ItemRegistry {
    public static final DeferredRegister.Items REGISTRAR = DeferredRegister.createItems(ThaumicRecords.MOD_ID);

    // Simple Items
    // @formatter:off
    public static final DeferredItem<Item> AMBER = REGISTRAR.registerSimpleItem("amber");
    public static final DeferredItem<Item> QUICKSILVER = REGISTRAR.registerSimpleItem("quicksilver");
    public static final DeferredItem<Item> BATH_SALTS = REGISTRAR.registerSimpleItem("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = REGISTRAR.registerSimpleItem("primal_charm");
    public static final DeferredItem<Item> ENCHANTED_FABRIC = REGISTRAR.registerSimpleItem("enchanted_fabric");
    public static final DeferredItem<Item> COIN = REGISTRAR.registerSimpleItem("coin");
    public static final DeferredItem<Item> AER_SHARD = REGISTRAR.registerSimpleItem("aer_shard");
    public static final DeferredItem<Item> IGNIS_SHARD = REGISTRAR.registerSimpleItem("ignis_shard");
    public static final DeferredItem<Item> AQUA_SHARD = REGISTRAR.registerSimpleItem("aqua_shard");
    public static final DeferredItem<Item> TERRA_SHARD = REGISTRAR.registerSimpleItem("terra_shard");
    public static final DeferredItem<Item> ORDO_SHARD = REGISTRAR.registerSimpleItem("ordo_shard");
    public static final DeferredItem<Item> PERDITIO_SHARD = REGISTRAR.registerSimpleItem("perditio_shard");
    public static final DeferredItem<Item> BALANCED_SHARD = REGISTRAR.registerSimpleItem("balanced_shard");
    public static final DeferredItem<Item> SALIS_MUNDUS = REGISTRAR.registerSimpleItem("salis_mundus");
    public static final DeferredItem<Item> PRIMORDIAL_PEARL=REGISTRAR.registerItem("primordial_pearl", PrimordialPearItem::new, itemProp(1));


    public static final DeferredItem<Item> WAND_CAP_IRON = REGISTRAR.registerSimpleItem("wand_cap_iron");
    public static final DeferredItem<Item> WAND_CAP_GOLD = REGISTRAR.registerSimpleItem("wand_cap_gold");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM = REGISTRAR.registerSimpleItem("wand_cap_thaumium");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM_INERT = REGISTRAR.registerSimpleItem("wand_cap_thaumium_inert");
    public static final DeferredItem<Item> WAND_ROD_GREATWOOD = REGISTRAR.registerSimpleItem("wand_rod_greatwood");
    public static final DeferredItem<Item> WAND_ROD_SILVERWOOD = REGISTRAR.registerSimpleItem("wand_rod_silverwood");


    public static final DeferredItem<WandItem> WAND = REGISTRAR.register("wand", WandItem::new);

    public static final DeferredItem<ThaumometerItem> THAUMOMETER = REGISTRAR.register("thaumometer", ThaumometerItem::new);

    public static final DeferredItem<GogglesItem> GOGGLES=REGISTRAR.register("goggles",GogglesItem::new);

    public static final DeferredItem<WispEssenceItem> WISP_ESSENCE=REGISTRAR.register("wisp_essence",WispEssenceItem::new);

    public static final DeferredItem<ScribingToolsItem> SCRIBING_TOOLS=REGISTRAR.register("scribing_tools",ScribingToolsItem::new);

    public static final DeferredItem<ResearchNotesItem> RESEARCH_NOTES=REGISTRAR.register("research_notes",ResearchNotesItem::new);

    // Block Items
    public static final DeferredItem<BlockItem> AER_INFUSED_STONE = REGISTRAR.register("aer_infused_stone",
            () -> new BlockItem(BlockRegistry.AER_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> IGNIS_INFUSED_STONE = REGISTRAR.register("ignis_infused_stone",
            () -> new BlockItem(BlockRegistry.IGNIS_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> AQUA_INFUSED_STONE = REGISTRAR.register("aqua_infused_stone",
            () -> new BlockItem(BlockRegistry.AQUA_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TERRA_INFUSED_STONE = REGISTRAR.register("terra_infused_stone",
            () -> new BlockItem(BlockRegistry.TERRA_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ORDO_INFUSED_STONE = REGISTRAR.register("ordo_infused_stone",
            () -> new BlockItem(BlockRegistry.ORDO_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> PERDITIO_INFUSED_STONE = REGISTRAR.register("perditio_infused_stone",
            () -> new BlockItem(BlockRegistry.PERDITIO_INFUSED_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> AMBER_ORE = REGISTRAR.register("amber_ore",
            () -> new BlockItem(BlockRegistry.AMBER_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CINNABAR_ORE = REGISTRAR.register("cinnabar_ore",
            () -> new BlockItem(BlockRegistry.CINNABAR_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> AURA_NODE = REGISTRAR.register("aura_node",
            () -> new BlockItem(BlockRegistry.AURA_NODE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_WORKBENCH = REGISTRAR.register("arcane_workbench",
            () -> new BlockItem(BlockRegistry.ARCANE_WORKBENCH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TABLE = REGISTRAR.register("table",
            () -> new BlockItem(BlockRegistry.TABLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_PEDESTAL = REGISTRAR.register("arcane_pedestal",
            () -> new BlockItem(BlockRegistry.ARCANE_PEDESTAL.get(), new Item.Properties()));
    public static final DeferredItem<Item> SILVERWOOD_SAPLING = ItemRegistry.REGISTRAR.register("silverwood_sapling",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_SAPLING.get(), new Item.Properties()));
    public static final DeferredItem<Item> SILVERWOOD_LOG = ItemRegistry.REGISTRAR.register("silverwood_log",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> SILVERWOOD_LEAVES = ItemRegistry.REGISTRAR.register("silverwood_leaves",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_LEAVES.get(), new Item.Properties()));
    public static final DeferredItem<Item> GREATWOOD_SAPLING = ItemRegistry.REGISTRAR.register("greatwood_sapling",
            () -> new BlockItem(BlockRegistry.GREATWOOD_SAPLING.get(), new Item.Properties()));
    public static final DeferredItem<Item> GREATWOOD_LOG = ItemRegistry.REGISTRAR.register("greatwood_log",
            () -> new BlockItem(BlockRegistry.GREATWOOD_LOG.get(), new Item.Properties()));
    public static final DeferredItem<Item> GREATWOOD_LEAVES = ItemRegistry.REGISTRAR.register("greatwood_leaves",
            () -> new BlockItem(BlockRegistry.GREATWOOD_LEAVES.get(), new Item.Properties()));
    // @formatter:on
    public static void putInCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(ironCappedWoodWand());
        output.accept(goldCappedGreatwoodWand());
        output.accept(thaumiumCappedSilverwoodWand());
        output.accept(THAUMOMETER);
        output.accept(AER_INFUSED_STONE);
        output.accept(IGNIS_INFUSED_STONE);
        output.accept(AQUA_INFUSED_STONE);
        output.accept(TERRA_INFUSED_STONE);
        output.accept(ORDO_INFUSED_STONE);
        output.accept(PERDITIO_INFUSED_STONE);
        output.accept(AMBER_ORE);
        output.accept(AMBER);
        output.accept(CINNABAR_ORE);
        output.accept(QUICKSILVER);
        output.accept(BATH_SALTS);
        output.accept(PRIMAL_CHARM);
        output.accept(ENCHANTED_FABRIC);
        output.accept(COIN);
        output.accept(AER_SHARD);
        output.accept(IGNIS_SHARD);
        output.accept(AQUA_SHARD);
        output.accept(TERRA_SHARD);
        output.accept(ORDO_SHARD);
        output.accept(PERDITIO_SHARD);
        output.accept(BALANCED_SHARD);
        output.accept(SALIS_MUNDUS);
        output.accept(WAND_CAP_IRON);
        output.accept(WAND_CAP_GOLD);
        output.accept(WAND_CAP_THAUMIUM);
        output.accept(WAND_CAP_THAUMIUM_INERT);
        output.accept(WAND_ROD_GREATWOOD);
        output.accept(WAND_ROD_SILVERWOOD);
        output.accept(GOGGLES);
        output.accept(TABLE);
        output.accept(SCRIBING_TOOLS);
        output.accept(ARCANE_WORKBENCH);
        output.accept(SILVERWOOD_SAPLING);
        output.accept(SILVERWOOD_LOG);
        output.accept(SILVERWOOD_LEAVES);
        output.accept(GREATWOOD_SAPLING);
        output.accept(GREATWOOD_LOG);
        output.accept(GREATWOOD_LEAVES);
        output.accept(ARCANE_PEDESTAL);
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
        for (ResourceLocation aspect : Aspect.getPrimalList()) {
            initialAspects.put(aspect, wandRod.getCapacity());
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), initialAspects);
        initialWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return initialWand;
    }

    private static ItemStack goldCappedGreatwoodWand() {
        ItemStack initialWand = new ItemStack(WAND.get());
        AspectList initialAspects = new AspectList();
        WandRod wandRod = WandRodRegistry.WAND_ROD_GREATWOOD.get();
        for (ResourceLocation aspect : Aspect.getPrimalList()) {
            initialAspects.put(aspect, wandRod.getCapacity());
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
        for (ResourceLocation aspect : Aspect.getPrimalList()) {
            initialAspects.put(aspect, wandRod.getCapacity());
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_SILVERWOOD.getId(), WandCapRegistry.WAND_CAP_THAUMIUM.getId(),
                initialAspects);
        initialWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return initialWand;
    }

    private static Item.Properties itemProp(int stackSize) {
        return new Item.Properties().stacksTo(stackSize);
    }
}