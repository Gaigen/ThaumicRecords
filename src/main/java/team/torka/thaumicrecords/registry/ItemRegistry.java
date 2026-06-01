package team.torka.thaumicrecords.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
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
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.item.BootsTravellerItem;
import team.torka.thaumicrecords.item.CrimsonBladeItem;
import team.torka.thaumicrecords.item.CultistBootsItem;
import team.torka.thaumicrecords.item.CultistLeaderArmorItem;
import team.torka.thaumicrecords.item.CultistPlateArmorItem;
import team.torka.thaumicrecords.item.CultistRobeArmorItem;
import team.torka.thaumicrecords.item.ElementalAxeItem;
import team.torka.thaumicrecords.item.ElementalHoeItem;
import team.torka.thaumicrecords.item.ElementalPickaxeItem;
import team.torka.thaumicrecords.item.ElementalShovelItem;
import team.torka.thaumicrecords.item.FortressArmorItem;
import team.torka.thaumicrecords.item.GogglesItem;
import team.torka.thaumicrecords.item.JarBlockItem;
import team.torka.thaumicrecords.item.KnowledgeFragmentItem;
import team.torka.thaumicrecords.item.PhialItem;
import team.torka.thaumicrecords.item.PrimalCrusherItem;
import team.torka.thaumicrecords.item.PrimordialPearItem;
import team.torka.thaumicrecords.item.ResearchNotesItem;
import team.torka.thaumicrecords.item.RobeArmorItem;
import team.torka.thaumicrecords.item.ScribingToolsItem;
import team.torka.thaumicrecords.item.ThaumiumArmorItem;
import team.torka.thaumicrecords.item.ThaumiumAxeItem;
import team.torka.thaumicrecords.item.ThaumiumHoeItem;
import team.torka.thaumicrecords.item.ThaumiumPickaxeItem;
import team.torka.thaumicrecords.item.ThaumiumShovelItem;
import team.torka.thaumicrecords.item.ThaumiumSwordItem;
import team.torka.thaumicrecords.item.ThaumometerItem;
import team.torka.thaumicrecords.item.ThaumonomiconItem;
import team.torka.thaumicrecords.item.VoidArmorItem;
import team.torka.thaumicrecords.item.VoidAxeItem;
import team.torka.thaumicrecords.item.VoidHoeItem;
import team.torka.thaumicrecords.item.VoidPickaxeItem;
import team.torka.thaumicrecords.item.VoidRobeArmorItem;
import team.torka.thaumicrecords.item.VoidShovelItem;
import team.torka.thaumicrecords.item.VoidSwordItem;
import team.torka.thaumicrecords.item.WandItem;
import team.torka.thaumicrecords.item.WispEssenceItem;
import team.torka.thaumicrecords.item.CurioItem;
import team.torka.thaumicrecords.item.RunicCurioItem;

public class ItemRegistry {
    public static final DeferredRegister.Items REGISTRAR = DeferredRegister.createItems(ThaumicRecords.MOD_ID);

    // Simple Items
    // @formatter:off
    public static final DeferredItem<Item> AMBER = REGISTRAR.registerSimpleItem("amber");
    public static final DeferredItem<Item> QUICKSILVER = REGISTRAR.registerSimpleItem("quicksilver");
    public static final DeferredItem<Item> BATH_SALTS = REGISTRAR.registerSimpleItem("bath_salts");
    public static final DeferredItem<Item> PRIMAL_CHARM = REGISTRAR.registerSimpleItem("primal_charm");
    public static final DeferredItem<Item> PHIAL = REGISTRAR.register("phial",PhialItem::new);
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
    public static final DeferredItem<Item> PRIMORDIAL_PEARL = REGISTRAR.registerItem("primordial_pearl", PrimordialPearItem::new, itemProp(1));
    public static final DeferredItem<GogglesItem> GOGGLES = REGISTRAR.register("goggles",GogglesItem::new);
    public static final DeferredItem<ThaumonomiconItem> THAUMONOMICON = REGISTRAR.register("thaumonomicon",ThaumonomiconItem::new);

    // Fortress Armor
    public static final DeferredItem<FortressArmorItem> FORTRESS_HELMET = REGISTRAR.register("fortress_helmet",
            () -> new FortressArmorItem(ArmorItem.Type.HELMET, ArmorMaterialRegistry.FORTRESS));
    public static final DeferredItem<FortressArmorItem> FORTRESS_CHESTPLATE = REGISTRAR.register("fortress_chestplate",
            () -> new FortressArmorItem(ArmorItem.Type.CHESTPLATE, ArmorMaterialRegistry.FORTRESS));
    public static final DeferredItem<FortressArmorItem> FORTRESS_LEGGINGS = REGISTRAR.register("fortress_leggings",
            () -> new FortressArmorItem(ArmorItem.Type.LEGGINGS, ArmorMaterialRegistry.FORTRESS));

    // Boots of the Traveller
    public static final DeferredItem<BootsTravellerItem> BOOTS_TRAVELLER = REGISTRAR.register("boots_traveller",
            () -> new BootsTravellerItem(ArmorItem.Type.BOOTS, ArmorMaterialRegistry.TRAVELLER));

    // Crimson Cultist Robe Armor
    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_HELMET = REGISTRAR.register("crimson_robe_helmet",
            () -> new CultistRobeArmorItem(ArmorMaterialRegistry.CULTIST_CLOTH, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_CHESTPLATE = REGISTRAR.register("crimson_robe_chestplate",
            () -> new CultistRobeArmorItem(ArmorMaterialRegistry.CULTIST_CLOTH, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistRobeArmorItem> CRIMSON_ROBE_LEGGINGS = REGISTRAR.register("crimson_robe_leggings",
            () -> new CultistRobeArmorItem(ArmorMaterialRegistry.CULTIST_CLOTH, ArmorItem.Type.LEGGINGS));

    // Crimson Cultist Plate Armor
    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_HELMET = REGISTRAR.register("crimson_plate_helmet",
            () -> new CultistPlateArmorItem(ArmorMaterialRegistry.CULTIST_PLATE, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_CHESTPLATE = REGISTRAR.register("crimson_plate_chestplate",
            () -> new CultistPlateArmorItem(ArmorMaterialRegistry.CULTIST_PLATE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistPlateArmorItem> CRIMSON_PLATE_LEGGINGS = REGISTRAR.register("crimson_plate_leggings",
            () -> new CultistPlateArmorItem(ArmorMaterialRegistry.CULTIST_PLATE, ArmorItem.Type.LEGGINGS));

    // Crimson Cultist Leader Armor
    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_HELMET = REGISTRAR.register("crimson_leader_helmet",
            () -> new CultistLeaderArmorItem(ArmorMaterialRegistry.CULTIST_LEADER, ArmorItem.Type.HELMET));
    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_CHESTPLATE = REGISTRAR.register("crimson_leader_chestplate",
            () -> new CultistLeaderArmorItem(ArmorMaterialRegistry.CULTIST_LEADER, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CultistLeaderArmorItem> CRIMSON_LEADER_LEGGINGS = REGISTRAR.register("crimson_leader_leggings",
            () -> new CultistLeaderArmorItem(ArmorMaterialRegistry.CULTIST_LEADER, ArmorItem.Type.LEGGINGS));

    // Crimson Cultist Boots
    public static final DeferredItem<CultistBootsItem> CRIMSON_BOOTS = REGISTRAR.register("crimson_boots",
            () -> new CultistBootsItem(ArmorMaterialRegistry.CULTIST_CLOTH, ArmorItem.Type.BOOTS));

    // Void Robe Armor
    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_HELMET = REGISTRAR.register("void_robe_helmet",
            () -> new VoidRobeArmorItem(ArmorMaterialRegistry.VOID_ROBE, ArmorItem.Type.HELMET));
    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_CHESTPLATE = REGISTRAR.register("void_robe_chestplate",
            () -> new VoidRobeArmorItem(ArmorMaterialRegistry.VOID_ROBE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<VoidRobeArmorItem> VOID_ROBE_LEGGINGS = REGISTRAR.register("void_robe_leggings",
            () -> new VoidRobeArmorItem(ArmorMaterialRegistry.VOID_ROBE, ArmorItem.Type.LEGGINGS));

    // Ingots
    public static final DeferredItem<Item> THAUMIUM_INGOT = REGISTRAR.registerSimpleItem("thaumium_ingot");
    public static final DeferredItem<Item> VOID_INGOT = REGISTRAR.registerSimpleItem("void_ingot");

    // Metal Nuggets
    public static final DeferredItem<Item> NUGGET_THAUMIUM = REGISTRAR.registerSimpleItem("nugget_thaumium");
    public static final DeferredItem<Item> NUGGET_VOID = REGISTRAR.registerSimpleItem("nugget_void");
    public static final DeferredItem<Item> NUGGET_QUICKSILVER = REGISTRAR.registerSimpleItem("nugget_quicksilver");
    public static final DeferredItem<Item> TALLOW = REGISTRAR.registerSimpleItem("tallow");
    public static final DeferredItem<Item> VOID_SEED = REGISTRAR.registerSimpleItem("void_seed");
    public static final DeferredItem<Item> ZOMBIE_BRAIN = REGISTRAR.register("zombie_brain",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4).saturationModifier(0.2f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8f).build())));
    public static final DeferredItem<KnowledgeFragmentItem> KNOWLEDGE_FRAGMENT = REGISTRAR.register("knowledge_fragment",
            () -> new KnowledgeFragmentItem());
    public static final DeferredItem<Item> ESSENTIA_FILTER = REGISTRAR.registerSimpleItem("essentia_filter");
    public static final DeferredItem<Item> MIRROR_GLASS = REGISTRAR.registerSimpleItem("mirror_glass");
    public static final DeferredItem<Item> TAINT_SLIME = REGISTRAR.registerSimpleItem("taint_slime");
    public static final DeferredItem<Item> TAINT_TENDRIL = REGISTRAR.registerSimpleItem("taint_tendril");

    // Edible Nuggets (food: 1 nutrition, 0.3 saturation)
    private static final FoodProperties NUGGET_FOOD = new FoodProperties.Builder().nutrition(1).saturationModifier(0.3f).build();
    public static final DeferredItem<Item> NUGGET_CHICKEN = REGISTRAR.register("nugget_chicken",
            () -> new Item(new Item.Properties().food(NUGGET_FOOD)));
    public static final DeferredItem<Item> NUGGET_BEEF = REGISTRAR.register("nugget_beef",
            () -> new Item(new Item.Properties().food(NUGGET_FOOD)));
    public static final DeferredItem<Item> NUGGET_PORK = REGISTRAR.register("nugget_pork",
            () -> new Item(new Item.Properties().food(NUGGET_FOOD)));
    public static final DeferredItem<Item> NUGGET_FISH = REGISTRAR.register("nugget_fish",
            () -> new Item(new Item.Properties().food(NUGGET_FOOD)));

    // Triple Meat Treat (food: 6 nutrition, 0.8 saturation, always edible, regeneration)
    private static final FoodProperties TRIPLE_MEAT_TREAT_FOOD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8f).alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.66f).build();
    public static final DeferredItem<Item> TRIPLE_MEAT_TREAT = REGISTRAR.register("triple_meat_treat",
            () -> new Item(new Item.Properties().food(TRIPLE_MEAT_TREAT_FOOD)));

    // Thaumium Armor
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_HELMET = REGISTRAR.register("thaumium_helmet",
            () -> new ThaumiumArmorItem(ArmorItem.Type.HELMET, ArmorMaterialRegistry.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_CHESTPLATE = REGISTRAR.register("thaumium_chestplate",
            () -> new ThaumiumArmorItem(ArmorItem.Type.CHESTPLATE, ArmorMaterialRegistry.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_LEGGINGS = REGISTRAR.register("thaumium_leggings",
            () -> new ThaumiumArmorItem(ArmorItem.Type.LEGGINGS, ArmorMaterialRegistry.THAUMIUM));
    public static final DeferredItem<ThaumiumArmorItem> THAUMIUM_BOOTS = REGISTRAR.register("thaumium_boots",
            () -> new ThaumiumArmorItem(ArmorItem.Type.BOOTS, ArmorMaterialRegistry.THAUMIUM));

    // Void Armor
    public static final DeferredItem<VoidArmorItem> VOID_HELMET = REGISTRAR.register("void_helmet",
            () -> new VoidArmorItem(ArmorItem.Type.HELMET, ArmorMaterialRegistry.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_CHESTPLATE = REGISTRAR.register("void_chestplate",
            () -> new VoidArmorItem(ArmorItem.Type.CHESTPLATE, ArmorMaterialRegistry.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_LEGGINGS = REGISTRAR.register("void_leggings",
            () -> new VoidArmorItem(ArmorItem.Type.LEGGINGS, ArmorMaterialRegistry.VOID));
    public static final DeferredItem<VoidArmorItem> VOID_BOOTS = REGISTRAR.register("void_boots",
            () -> new VoidArmorItem(ArmorItem.Type.BOOTS, ArmorMaterialRegistry.VOID));

    // Thaumium Tools
    public static final DeferredItem<ThaumiumPickaxeItem> THAUMIUM_PICKAXE = REGISTRAR.register("thaumium_pickaxe", ThaumiumPickaxeItem::new);
    public static final DeferredItem<ThaumiumSwordItem> THAUMIUM_SWORD = REGISTRAR.register("thaumium_sword", ThaumiumSwordItem::new);
    public static final DeferredItem<ThaumiumAxeItem> THAUMIUM_AXE = REGISTRAR.register("thaumium_axe", ThaumiumAxeItem::new);
    public static final DeferredItem<ThaumiumShovelItem> THAUMIUM_SHOVEL = REGISTRAR.register("thaumium_shovel", ThaumiumShovelItem::new);
    public static final DeferredItem<ThaumiumHoeItem> THAUMIUM_HOE = REGISTRAR.register("thaumium_hoe", ThaumiumHoeItem::new);

    // Void Tools
    public static final DeferredItem<VoidPickaxeItem> VOID_PICKAXE = REGISTRAR.register("void_pickaxe", VoidPickaxeItem::new);
    public static final DeferredItem<VoidSwordItem> VOID_SWORD = REGISTRAR.register("void_sword", VoidSwordItem::new);
    public static final DeferredItem<VoidAxeItem> VOID_AXE = REGISTRAR.register("void_axe", VoidAxeItem::new);
    public static final DeferredItem<VoidShovelItem> VOID_SHOVEL = REGISTRAR.register("void_shovel", VoidShovelItem::new);
    public static final DeferredItem<VoidHoeItem> VOID_HOE = REGISTRAR.register("void_hoe", VoidHoeItem::new);

    // Crimson Blade
    public static final DeferredItem<CrimsonBladeItem> CRIMSON_BLADE = REGISTRAR.register("crimson_blade", CrimsonBladeItem::new);

    // Primal Crusher
    public static final DeferredItem<PrimalCrusherItem> PRIMAL_CRUSHER = REGISTRAR.register("primal_crusher", PrimalCrusherItem::new);

    // Elemental Tools
    public static final DeferredItem<ElementalShovelItem> ELEMENTAL_SHOVEL = REGISTRAR.register("elemental_shovel", ElementalShovelItem::new);
    public static final DeferredItem<ElementalPickaxeItem> ELEMENTAL_PICKAXE = REGISTRAR.register("elemental_pickaxe", ElementalPickaxeItem::new);
    public static final DeferredItem<ElementalAxeItem> ELEMENTAL_AXE = REGISTRAR.register("elemental_axe", ElementalAxeItem::new);

    public static final DeferredItem<ElementalHoeItem> ELEMENTAL_HOE = REGISTRAR.register("elemental_hoe", ElementalHoeItem::new);

    // Robe Armor
    public static final DeferredItem<RobeArmorItem> ROBE_CHESTPLATE = REGISTRAR.register("robe_chestplate",
            () -> new RobeArmorItem(ArmorItem.Type.CHESTPLATE, ArmorMaterialRegistry.ROBE));
    public static final DeferredItem<RobeArmorItem> ROBE_LEGGINGS = REGISTRAR.register("robe_leggings",
            () -> new RobeArmorItem(ArmorItem.Type.LEGGINGS, ArmorMaterialRegistry.ROBE));
    public static final DeferredItem<RobeArmorItem> ROBE_BOOTS = REGISTRAR.register("robe_boots",
            () -> new RobeArmorItem(ArmorItem.Type.BOOTS, ArmorMaterialRegistry.ROBE));

    public static final DeferredItem<Item> WAND_CAP_IRON = REGISTRAR.registerSimpleItem("wand_cap_iron");
    public static final DeferredItem<Item> WAND_CAP_GOLD = REGISTRAR.registerSimpleItem("wand_cap_gold");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM = REGISTRAR.registerSimpleItem("wand_cap_thaumium");
    public static final DeferredItem<Item> WAND_CAP_THAUMIUM_INERT = REGISTRAR.registerSimpleItem("wand_cap_thaumium_inert");
    public static final DeferredItem<Item> WAND_CAP_COPPER = REGISTRAR.registerSimpleItem("wand_cap_copper");
    public static final DeferredItem<Item> WAND_CAP_SILVER = REGISTRAR.registerSimpleItem("wand_cap_silver");
    public static final DeferredItem<Item> WAND_CAP_VOID = REGISTRAR.registerSimpleItem("wand_cap_void");
    public static final DeferredItem<Item> WAND_CAP_SILVER_INERT = REGISTRAR.registerSimpleItem("wand_cap_silver_inert");
    public static final DeferredItem<Item> WAND_CAP_VOID_INERT = REGISTRAR.registerSimpleItem("wand_cap_void_inert");
    public static final DeferredItem<Item> WAND_ROD_GREATWOOD = REGISTRAR.registerSimpleItem("wand_rod_greatwood");
    public static final DeferredItem<Item> WAND_ROD_SILVERWOOD = REGISTRAR.registerSimpleItem("wand_rod_silverwood");
    public static final DeferredItem<Item> WAND_ROD_OBSIDIAN = REGISTRAR.registerSimpleItem("wand_rod_obsidian");
    public static final DeferredItem<Item> WAND_ROD_BLAZE = REGISTRAR.registerSimpleItem("wand_rod_blaze");
    public static final DeferredItem<Item> WAND_ROD_ICE = REGISTRAR.registerSimpleItem("wand_rod_ice");
    public static final DeferredItem<Item> WAND_ROD_QUARTZ = REGISTRAR.registerSimpleItem("wand_rod_quartz");
    public static final DeferredItem<Item> WAND_ROD_BONE = REGISTRAR.registerSimpleItem("wand_rod_bone");
    public static final DeferredItem<Item> WAND_ROD_REED = REGISTRAR.registerSimpleItem("wand_rod_reed");
    public static final DeferredItem<Item> STAFF_ROD_GREATWOOD = REGISTRAR.registerSimpleItem("staff_rod_greatwood");
    public static final DeferredItem<Item> STAFF_ROD_OBSIDIAN = REGISTRAR.registerSimpleItem("staff_rod_obsidian");
    public static final DeferredItem<Item> STAFF_ROD_BLAZE = REGISTRAR.registerSimpleItem("staff_rod_blaze");
    public static final DeferredItem<Item> STAFF_ROD_ICE = REGISTRAR.registerSimpleItem("staff_rod_ice");
    public static final DeferredItem<Item> STAFF_ROD_QUARTZ = REGISTRAR.registerSimpleItem("staff_rod_quartz");
    public static final DeferredItem<Item> STAFF_ROD_BONE = REGISTRAR.registerSimpleItem("staff_rod_bone");
    public static final DeferredItem<Item> STAFF_ROD_REED = REGISTRAR.registerSimpleItem("staff_rod_reed");
    public static final DeferredItem<Item> STAFF_ROD_SILVERWOOD = REGISTRAR.registerSimpleItem("staff_rod_silverwood");
    public static final DeferredItem<Item> STAFF_ROD_PRIMAL = REGISTRAR.registerSimpleItem("staff_rod_primal");


    public static final DeferredItem<WandItem> WAND = REGISTRAR.register("wand", WandItem::new);

    public static final DeferredItem<ThaumometerItem> THAUMOMETER = REGISTRAR.register("thaumometer", ThaumometerItem::new);


    public static final DeferredItem<WispEssenceItem> WISP_ESSENCE=REGISTRAR.register("wisp_essence",WispEssenceItem::new);

    public static final DeferredItem<ScribingToolsItem> SCRIBING_TOOLS=REGISTRAR.register("scribing_tools",ScribingToolsItem::new);

    public static final DeferredItem<ResearchNotesItem> RESEARCH_NOTES=REGISTRAR.register("research_notes",ResearchNotesItem::new);

    // Curios - Mundane Baubles
    public static final DeferredItem<CurioItem> MUNDANE_RING = REGISTRAR.register("mundane_ring",
            () -> new CurioItem("ring"));
    public static final DeferredItem<CurioItem> MUNDANE_AMULET = REGISTRAR.register("mundane_amulet",
            () -> new CurioItem("necklace"));
    public static final DeferredItem<CurioItem> MUNDANE_BELT = REGISTRAR.register("mundane_belt",
            () -> new CurioItem("belt"));

    // Runic Baubles - Rings
    public static final DeferredItem<RunicCurioItem> RUNIC_RING_LESSER = REGISTRAR.register("runic_ring_lesser",
            () -> new RunicCurioItem("ring", 1));
    public static final DeferredItem<RunicCurioItem> RUNIC_RING = REGISTRAR.register("runic_ring",
            () -> new RunicCurioItem("ring", 5));
    public static final DeferredItem<RunicCurioItem> RUNIC_RING_CHARGED = REGISTRAR.register("runic_ring_charged",
            () -> new RunicCurioItem("ring", 4));
    public static final DeferredItem<RunicCurioItem> RUNIC_RING_REGEN = REGISTRAR.register("runic_ring_regen",
            () -> new RunicCurioItem("ring", 4));

    // Runic Baubles - Amulets
    public static final DeferredItem<RunicCurioItem> RUNIC_AMULET = REGISTRAR.register("runic_amulet",
            () -> new RunicCurioItem("necklace", 8));
    public static final DeferredItem<RunicCurioItem> RUNIC_AMULET_EMERGENCY = REGISTRAR.register("runic_amulet_emergency",
            () -> new RunicCurioItem("necklace", 7));

    // Runic Baubles - Girdles
    public static final DeferredItem<RunicCurioItem> RUNIC_GIRDLE = REGISTRAR.register("runic_girdle",
            () -> new RunicCurioItem("belt", 10));
    public static final DeferredItem<RunicCurioItem> RUNIC_GIRDLE_KINETIC = REGISTRAR.register("runic_girdle_kinetic",
            () -> new RunicCurioItem("belt", 9));

    // Block Items
    public static final DeferredItem<BlockItem> PAVING_STONE_OF_TRAVEL = REGISTRAR.register("paving_stone_of_travel",
            () -> new BlockItem(BlockRegistry.PAVING_STONE_OF_TRAVEL.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> PAVING_STONE_OF_WARDING = REGISTRAR.register("paving_stone_of_warding",
            () -> new BlockItem(BlockRegistry.PAVING_STONE_OF_WARDING.get(), new Item.Properties()));
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
    public static final DeferredItem<Item> CINDERPEARL = REGISTRAR.register("cinderpearl", ()-> new BlockItem(BlockRegistry.CINDERPEARL.get(), new Item.Properties()));
    public static final DeferredItem<Item> SHIMMERLEAF = REGISTRAR.register("shimmerleaf", ()-> new BlockItem(BlockRegistry.SHIMMERLEAF.get(), new Item.Properties()));

    // Ore Clusters
    public static final DeferredItem<Item> CLUSTER_IRON = REGISTRAR.registerSimpleItem("cluster_iron");
    public static final DeferredItem<Item> CLUSTER_GOLD = REGISTRAR.registerSimpleItem("cluster_gold");
    public static final DeferredItem<Item> CLUSTER_COPPER = REGISTRAR.registerSimpleItem("cluster_copper");
    public static final DeferredItem<Item> CLUSTER_CINNABAR = REGISTRAR.registerSimpleItem("cluster_cinnabar");
    public static final DeferredItem<Item> CLUSTER_TIN = REGISTRAR.registerSimpleItem("cluster_tin");
    public static final DeferredItem<Item> CLUSTER_SILVER = REGISTRAR.registerSimpleItem("cluster_silver");
    public static final DeferredItem<Item> CLUSTER_LEAD = REGISTRAR.registerSimpleItem("cluster_lead");
    public static final DeferredItem<Item> CLUSTER_QUARTZ = REGISTRAR.registerSimpleItem("cluster_quartz");
    public static final DeferredItem<BlockItem> ARCANE_WORKBENCH = REGISTRAR.register("arcane_workbench",
            () -> new BlockItem(BlockRegistry.ARCANE_WORKBENCH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TABLE = REGISTRAR.register("table",
            () -> new BlockItem(BlockRegistry.TABLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DECONSTRUCTION_TABLE = REGISTRAR.register("deconstruction_table",
            () -> new BlockItem(BlockRegistry.DECONSTRUCTION_TABLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> HUNGRY_CHEST = REGISTRAR.register("hungry_chest",
            () -> new BlockItem(BlockRegistry.HUNGRY_CHEST.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> INFUSION_PILLAR = REGISTRAR.register("infusion_pillar",
            () -> new BlockItem(BlockRegistry.INFUSION_PILLAR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_PEDESTAL = REGISTRAR.register("arcane_pedestal",
            () -> new BlockItem(BlockRegistry.ARCANE_PEDESTAL.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CRUCIBLE = REGISTRAR.register("crucible",
            () -> new BlockItem(BlockRegistry.CRUCIBLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> THAUMIUM_BLOCK = REGISTRAR.register("thaumium_block",
            () -> new BlockItem(BlockRegistry.THAUMIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> VOID_BLOCK = REGISTRAR.register("void_block",
            () -> new BlockItem(BlockRegistry.VOID_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> AMBER_BLOCK = REGISTRAR.register("amber_block",
            () -> new BlockItem(BlockRegistry.AMBER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> AMBER_BRICK = REGISTRAR.register("amber_brick",
            () -> new BlockItem(BlockRegistry.AMBER_BRICK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> OBSIDIAN_TILE = REGISTRAR.register("obsidian_tile",
            () -> new BlockItem(BlockRegistry.OBSIDIAN_TILE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TALLOW_BLOCK = REGISTRAR.register("tallow_block",
            () -> new BlockItem(BlockRegistry.TALLOW_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_STONE = REGISTRAR.register("arcane_stone",
            () -> new BlockItem(BlockRegistry.ARCANE_STONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_STONE_BRICK = REGISTRAR.register("arcane_stone_brick",
            () -> new BlockItem(BlockRegistry.ARCANE_STONE_BRICK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> FLESH_BLOCK = REGISTRAR.register("flesh_block",
            () -> new BlockItem(BlockRegistry.FLESH_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> GREATWOOD_PLANKS = REGISTRAR.register("greatwood_planks",
            () -> new BlockItem(BlockRegistry.GREATWOOD_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> SILVERWOOD_PLANKS = REGISTRAR.register("silverwood_planks",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_STONE_STAIRS = REGISTRAR.register("arcane_stone_stairs",
            () -> new BlockItem(BlockRegistry.ARCANE_STONE_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> GREATWOOD_STAIRS = REGISTRAR.register("greatwood_stairs",
            () -> new BlockItem(BlockRegistry.GREATWOOD_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> SILVERWOOD_STAIRS = REGISTRAR.register("silverwood_stairs",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ARCANE_STONE_SLAB = REGISTRAR.register("arcane_stone_slab",
            () -> new BlockItem(BlockRegistry.ARCANE_STONE_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> GREATWOOD_SLAB = REGISTRAR.register("greatwood_slab",
            () -> new BlockItem(BlockRegistry.GREATWOOD_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> SILVERWOOD_SLAB = REGISTRAR.register("silverwood_slab",
            () -> new BlockItem(BlockRegistry.SILVERWOOD_SLAB.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ALCHEMICAL_CONSTRUCT = REGISTRAR.register("alchemical_construct",
            () -> new BlockItem(BlockRegistry.ALCHEMICAL_CONSTRUCT.get(), new Item.Properties()));
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
    public static final DeferredItem<Item> JAR = ItemRegistry.REGISTRAR.register("jar", ()-> new JarBlockItem(BlockRegistry.JAR.get(),new Item.Properties()));
    // @formatter:on
    public static void putInCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        output.accept(ironCappedWoodWand());
        output.accept(goldCappedGreatwoodWand());
        output.accept(thaumiumCappedSilverwoodWand());
        output.accept(thaumiumCappedSilverwoodSceptre());
        output.accept(THAUMOMETER);
        output.accept(THAUMONOMICON);
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
        output.accept(PRIMORDIAL_PEARL);
        output.accept(AURA_NODE);
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
        output.accept(WAND_CAP_COPPER);
        output.accept(WAND_CAP_SILVER_INERT);
        output.accept(WAND_CAP_SILVER);
        output.accept(WAND_CAP_THAUMIUM_INERT);
        output.accept(WAND_CAP_THAUMIUM);
        output.accept(WAND_CAP_VOID_INERT);
        output.accept(WAND_CAP_VOID);
        output.accept(WAND_ROD_GREATWOOD);
        output.accept(WAND_ROD_SILVERWOOD);
        output.accept(WAND_ROD_OBSIDIAN);
        output.accept(WAND_ROD_BLAZE);
        output.accept(WAND_ROD_ICE);
        output.accept(WAND_ROD_QUARTZ);
        output.accept(WAND_ROD_BONE);
        output.accept(WAND_ROD_REED);
        output.accept(STAFF_ROD_GREATWOOD);
        output.accept(STAFF_ROD_OBSIDIAN);
        output.accept(STAFF_ROD_BLAZE);
        output.accept(STAFF_ROD_ICE);
        output.accept(STAFF_ROD_QUARTZ);
        output.accept(STAFF_ROD_BONE);
        output.accept(STAFF_ROD_REED);
        output.accept(STAFF_ROD_SILVERWOOD);
        output.accept(STAFF_ROD_PRIMAL);
        output.accept(GOGGLES);
        output.accept(FORTRESS_HELMET);
        output.accept(FORTRESS_CHESTPLATE);
        output.accept(FORTRESS_LEGGINGS);
        output.accept(THAUMIUM_INGOT);
        output.accept(NUGGET_THAUMIUM);
        output.accept(NUGGET_VOID);
        output.accept(NUGGET_QUICKSILVER);
        output.accept(TALLOW);
        output.accept(VOID_SEED);
        output.accept(ZOMBIE_BRAIN);
        output.accept(KNOWLEDGE_FRAGMENT);
        output.accept(ESSENTIA_FILTER);
        output.accept(MIRROR_GLASS);
        output.accept(TAINT_SLIME);
        output.accept(TAINT_TENDRIL);
        output.accept(NUGGET_CHICKEN);
        output.accept(NUGGET_BEEF);
        output.accept(NUGGET_PORK);
        output.accept(NUGGET_FISH);
        output.accept(TRIPLE_MEAT_TREAT);
        output.accept(THAUMIUM_HELMET);
        output.accept(THAUMIUM_CHESTPLATE);
        output.accept(THAUMIUM_LEGGINGS);
        output.accept(THAUMIUM_BOOTS);
        output.accept(VOID_INGOT);
        output.accept(VOID_HELMET);
        output.accept(VOID_CHESTPLATE);
        output.accept(VOID_LEGGINGS);
        output.accept(VOID_BOOTS);
        output.accept(THAUMIUM_PICKAXE);
        output.accept(THAUMIUM_SWORD);
        output.accept(THAUMIUM_AXE);
        output.accept(THAUMIUM_SHOVEL);
        output.accept(THAUMIUM_HOE);
        output.accept(VOID_PICKAXE);
        output.accept(VOID_SWORD);
        output.accept(VOID_AXE);
        output.accept(VOID_SHOVEL);
        output.accept(VOID_HOE);
        output.accept(CRIMSON_BLADE);
        output.accept(PRIMAL_CRUSHER);
        output.accept(ELEMENTAL_SHOVEL);
        output.accept(ELEMENTAL_PICKAXE);
        output.accept(ELEMENTAL_AXE);
        output.accept(ELEMENTAL_HOE);
        output.accept(ROBE_CHESTPLATE);
        output.accept(ROBE_LEGGINGS);
        output.accept(ROBE_BOOTS);
        output.accept(CRIMSON_ROBE_HELMET);
        output.accept(CRIMSON_ROBE_CHESTPLATE);
        output.accept(CRIMSON_ROBE_LEGGINGS);
        output.accept(CRIMSON_PLATE_HELMET);
        output.accept(CRIMSON_PLATE_CHESTPLATE);
        output.accept(CRIMSON_PLATE_LEGGINGS);
        output.accept(CRIMSON_LEADER_HELMET);
        output.accept(CRIMSON_LEADER_CHESTPLATE);
        output.accept(CRIMSON_LEADER_LEGGINGS);
        output.accept(CRIMSON_BOOTS);
        output.accept(VOID_ROBE_HELMET);
        output.accept(VOID_ROBE_CHESTPLATE);
        output.accept(VOID_ROBE_LEGGINGS);
        output.accept(BOOTS_TRAVELLER);
        output.accept(TABLE);
        output.accept(DECONSTRUCTION_TABLE);
        output.accept(HUNGRY_CHEST);
        output.accept(INFUSION_PILLAR);
        output.accept(SCRIBING_TOOLS);
        output.accept(ARCANE_WORKBENCH);
        output.accept(SILVERWOOD_SAPLING);
        output.accept(SILVERWOOD_LOG);
        output.accept(SILVERWOOD_LEAVES);
        output.accept(GREATWOOD_SAPLING);
        output.accept(GREATWOOD_LOG);
        output.accept(GREATWOOD_LEAVES);
        output.accept(ARCANE_PEDESTAL);
        output.accept(CRUCIBLE);
        output.accept(ALCHEMICAL_CONSTRUCT);
        output.accept(JAR);
        output.accept(PRIMORDIAL_PEARL);
        output.accept(AURA_NODE);
        output.accept(PAVING_STONE_OF_TRAVEL);
        output.accept(PAVING_STONE_OF_WARDING);
        output.accept(CLUSTER_IRON);
        output.accept(CLUSTER_GOLD);
        output.accept(CLUSTER_COPPER);
        output.accept(CLUSTER_CINNABAR);
        output.accept(CLUSTER_TIN);
        output.accept(CLUSTER_SILVER);
        output.accept(CLUSTER_LEAD);
        output.accept(CLUSTER_QUARTZ);
        output.accept(THAUMIUM_BLOCK);
        output.accept(VOID_BLOCK);
        output.accept(AMBER_BLOCK);
        output.accept(AMBER_BRICK);
        output.accept(OBSIDIAN_TILE);
        output.accept(TALLOW_BLOCK);
        output.accept(ARCANE_STONE);
        output.accept(ARCANE_STONE_BRICK);
        output.accept(FLESH_BLOCK);
        output.accept(GREATWOOD_PLANKS);
        output.accept(SILVERWOOD_PLANKS);
        output.accept(ARCANE_STONE_STAIRS);
        output.accept(GREATWOOD_STAIRS);
        output.accept(SILVERWOOD_STAIRS);
        output.accept(ARCANE_STONE_SLAB);
        output.accept(GREATWOOD_SLAB);
        output.accept(SILVERWOOD_SLAB);
        output.accept(CINDERPEARL);
        output.accept(SHIMMERLEAF);
        output.accept(MUNDANE_RING);
        output.accept(MUNDANE_AMULET);
        output.accept(MUNDANE_BELT);
        output.accept(RUNIC_RING_LESSER);
        output.accept(RUNIC_RING);
        output.accept(RUNIC_RING_CHARGED);
        output.accept(RUNIC_RING_REGEN);
        output.accept(RUNIC_AMULET);
        output.accept(RUNIC_AMULET_EMERGENCY);
        output.accept(RUNIC_GIRDLE);
        output.accept(RUNIC_GIRDLE_KINETIC);
    }

    public static void putInPhialCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        ItemStack emptyPhial = new ItemStack(ItemRegistry.PHIAL.get());
        output.accept(emptyPhial);
        AspectRegistry.ASPECT_REGISTRY.forEach(aspect -> {
            ItemStack stack = new ItemStack(ItemRegistry.PHIAL.get());
            if (stack.getItem() instanceof PhialItem item) {
                AspectList list = new AspectList();
                list.put(AspectRegistry.ASPECT_REGISTRY.getKey(aspect), 8);
                item.setAspects(stack, list);
            }
            output.accept(stack);
        });
    }

    public static void putInWispEssenceCreativeTab(CreativeModeTab.ItemDisplayParameters p, CreativeModeTab.Output output) {
        AspectRegistry.ASPECT_REGISTRY.forEach(aspect -> {
            ItemStack stack = new ItemStack(ItemRegistry.WISP_ESSENCE.get());
            if (stack.getItem() instanceof WispEssenceItem item) {
                AspectList list = new AspectList();
                list.put(AspectRegistry.ASPECT_REGISTRY.getKey(aspect), 2);
                item.setAspects(stack, list);
            }
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

    private static ItemStack thaumiumCappedSilverwoodSceptre() {
        ItemStack sceptre = new ItemStack(WAND.get());
        AspectList initialAspects = new AspectList();
        int effectiveCapacity = (int) (WandRodRegistry.WAND_ROD_SILVERWOOD.get().getCapacity() * 1.5);
        for (ResourceLocation aspect : Aspect.getPrimalList()) {
            initialAspects.put(aspect, effectiveCapacity);
        }
        WandItemComponent initialData = new WandItemComponent(WandRodRegistry.WAND_ROD_SILVERWOOD.getId(), WandCapRegistry.WAND_CAP_THAUMIUM.getId(),
                initialAspects, true);
        sceptre.set(DataComponentRegistry.WAND_ITEM_DATA.get(), initialData);
        return sceptre;
    }

    private static Item.Properties itemProp(int stackSize) {
        return new Item.Properties().stacksTo(stackSize);
    }
}