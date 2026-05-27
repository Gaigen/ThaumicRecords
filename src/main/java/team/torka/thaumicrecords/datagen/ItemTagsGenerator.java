package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class ItemTagsGenerator extends ItemTagsProvider {
    public ItemTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags,
                             ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ThaumicRecords.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.SHARD).add(ItemRegistry.AER_SHARD.get()).add(ItemRegistry.IGNIS_SHARD.get()).add(ItemRegistry.AQUA_SHARD.get()).add(
                ItemRegistry.TERRA_SHARD.get()).add(ItemRegistry.ORDO_SHARD.get()).add(ItemRegistry.PERDITIO_SHARD.get()).add(
                ItemRegistry.BALANCED_SHARD.get());
        this.tag(ModTags.SHOW_NODE_WHEN_EQUIPPED).add(ItemRegistry.GOGGLES.get()).add(ItemRegistry.FORTRESS_HELMET.get());
        this.tag(ModTags.SHOW_NODE_WHEN_HELD).add(ItemRegistry.THAUMOMETER.get());
        // Armor enchantable tags
        // Thaumium
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.THAUMIUM_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.THAUMIUM_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.THAUMIUM_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ItemRegistry.THAUMIUM_BOOTS.get());
        // Void
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_BOOTS.get());
        // Fortress
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.FORTRESS_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.FORTRESS_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.FORTRESS_LEGGINGS.get());
        // Goggles
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.GOGGLES.get());
        // Boots of the Traveller
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ItemRegistry.BOOTS_TRAVELLER.get());
        // Robe
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.ROBE_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.ROBE_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ItemRegistry.ROBE_BOOTS.get());
        // Void Robe
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_ROBE_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_ROBE_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.VOID_ROBE_LEGGINGS.get());
        // Crimson Robe
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_ROBE_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_ROBE_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_ROBE_LEGGINGS.get());
        // Crimson Plate
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_PLATE_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_PLATE_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_PLATE_LEGGINGS.get());
        // Crimson Leader
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_LEADER_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_LEADER_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_LEADER_LEGGINGS.get());
        // Crimson Boots
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(ItemRegistry.CRIMSON_BOOTS.get());
        // General armor tag (all armor)
        this.tag(ItemTags.ARMOR_ENCHANTABLE).add(ItemRegistry.THAUMIUM_HELMET.get(), ItemRegistry.THAUMIUM_CHESTPLATE.get(),
                ItemRegistry.THAUMIUM_LEGGINGS.get(), ItemRegistry.THAUMIUM_BOOTS.get()).add(ItemRegistry.VOID_HELMET.get(), ItemRegistry.VOID_CHESTPLATE.get(),
                ItemRegistry.VOID_LEGGINGS.get(), ItemRegistry.VOID_BOOTS.get()).add(ItemRegistry.FORTRESS_HELMET.get(), ItemRegistry.FORTRESS_CHESTPLATE.get(),
                ItemRegistry.FORTRESS_LEGGINGS.get()).add(ItemRegistry.GOGGLES.get(), ItemRegistry.BOOTS_TRAVELLER.get()).add(
                ItemRegistry.ROBE_CHESTPLATE.get(), ItemRegistry.ROBE_LEGGINGS.get(), ItemRegistry.ROBE_BOOTS.get()).add(ItemRegistry.VOID_ROBE_HELMET.get(),
                ItemRegistry.VOID_ROBE_CHESTPLATE.get(), ItemRegistry.VOID_ROBE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_ROBE_HELMET.get(),
                ItemRegistry.CRIMSON_ROBE_CHESTPLATE.get(), ItemRegistry.CRIMSON_ROBE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_PLATE_HELMET.get(),
                ItemRegistry.CRIMSON_PLATE_CHESTPLATE.get(), ItemRegistry.CRIMSON_PLATE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_LEADER_HELMET.get(),
                ItemRegistry.CRIMSON_LEADER_CHESTPLATE.get(), ItemRegistry.CRIMSON_LEADER_LEGGINGS.get()).add(ItemRegistry.CRIMSON_BOOTS.get());
        // Durability enchantable (Unbreaking) — all armor + tools
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(ItemRegistry.THAUMIUM_HELMET.get(), ItemRegistry.THAUMIUM_CHESTPLATE.get(),
                ItemRegistry.THAUMIUM_LEGGINGS.get(), ItemRegistry.THAUMIUM_BOOTS.get()).add(ItemRegistry.VOID_HELMET.get(), ItemRegistry.VOID_CHESTPLATE.get(),
                ItemRegistry.VOID_LEGGINGS.get(), ItemRegistry.VOID_BOOTS.get()).add(ItemRegistry.FORTRESS_HELMET.get(), ItemRegistry.FORTRESS_CHESTPLATE.get(),
                ItemRegistry.FORTRESS_LEGGINGS.get()).add(ItemRegistry.GOGGLES.get(), ItemRegistry.BOOTS_TRAVELLER.get()).add(
                ItemRegistry.ROBE_CHESTPLATE.get(), ItemRegistry.ROBE_LEGGINGS.get(), ItemRegistry.ROBE_BOOTS.get()).add(ItemRegistry.VOID_ROBE_HELMET.get(),
                ItemRegistry.VOID_ROBE_CHESTPLATE.get(), ItemRegistry.VOID_ROBE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_ROBE_HELMET.get(),
                ItemRegistry.CRIMSON_ROBE_CHESTPLATE.get(), ItemRegistry.CRIMSON_ROBE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_PLATE_HELMET.get(),
                ItemRegistry.CRIMSON_PLATE_CHESTPLATE.get(), ItemRegistry.CRIMSON_PLATE_LEGGINGS.get()).add(ItemRegistry.CRIMSON_LEADER_HELMET.get(),
                ItemRegistry.CRIMSON_LEADER_CHESTPLATE.get(), ItemRegistry.CRIMSON_LEADER_LEGGINGS.get()).add(ItemRegistry.CRIMSON_BOOTS.get());
        // Tools enchantable tags
        this.tag(ItemTags.MINING_ENCHANTABLE).add(ItemRegistry.THAUMIUM_PICKAXE.get(), ItemRegistry.VOID_PICKAXE.get(), ItemRegistry.ELEMENTAL_PICKAXE.get(),
                ItemRegistry.PRIMAL_CRUSHER.get()).add(ItemRegistry.THAUMIUM_AXE.get(), ItemRegistry.VOID_AXE.get(), ItemRegistry.ELEMENTAL_AXE.get()).add(
                ItemRegistry.THAUMIUM_SHOVEL.get(), ItemRegistry.VOID_SHOVEL.get(), ItemRegistry.ELEMENTAL_SHOVEL.get()).add(ItemRegistry.THAUMIUM_HOE.get(),
                ItemRegistry.VOID_HOE.get(), ItemRegistry.ELEMENTAL_HOE.get());
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).add(ItemRegistry.THAUMIUM_PICKAXE.get(), ItemRegistry.VOID_PICKAXE.get(),
                ItemRegistry.ELEMENTAL_PICKAXE.get(), ItemRegistry.PRIMAL_CRUSHER.get()).add(ItemRegistry.THAUMIUM_AXE.get(), ItemRegistry.VOID_AXE.get(),
                ItemRegistry.ELEMENTAL_AXE.get()).add(ItemRegistry.THAUMIUM_SHOVEL.get(), ItemRegistry.VOID_SHOVEL.get(), ItemRegistry.ELEMENTAL_SHOVEL.get());
        this.tag(ItemTags.SWORD_ENCHANTABLE).add(ItemRegistry.THAUMIUM_SWORD.get(), ItemRegistry.VOID_SWORD.get(), ItemRegistry.CRIMSON_BLADE.get(),
                ItemRegistry.PRIMAL_CRUSHER.get());
        this.tag(ItemTags.WEAPON_ENCHANTABLE).add(ItemRegistry.THAUMIUM_SWORD.get(), ItemRegistry.VOID_SWORD.get(), ItemRegistry.CRIMSON_BLADE.get(),
                ItemRegistry.PRIMAL_CRUSHER.get()).add(ItemRegistry.THAUMIUM_AXE.get(), ItemRegistry.VOID_AXE.get(), ItemRegistry.ELEMENTAL_AXE.get()).add(
                ItemRegistry.THAUMIUM_PICKAXE.get(), ItemRegistry.VOID_PICKAXE.get(), ItemRegistry.ELEMENTAL_PICKAXE.get()).add(
                ItemRegistry.THAUMIUM_SHOVEL.get(), ItemRegistry.VOID_SHOVEL.get(), ItemRegistry.ELEMENTAL_SHOVEL.get());
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ItemRegistry.THAUMIUM_SWORD.get(), ItemRegistry.VOID_SWORD.get(), ItemRegistry.CRIMSON_BLADE.get(),
                ItemRegistry.PRIMAL_CRUSHER.get()).add(ItemRegistry.THAUMIUM_AXE.get(), ItemRegistry.VOID_AXE.get(), ItemRegistry.ELEMENTAL_AXE.get());
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(ItemRegistry.THAUMIUM_SWORD.get(), ItemRegistry.VOID_SWORD.get(), ItemRegistry.CRIMSON_BLADE.get(),
                ItemRegistry.PRIMAL_CRUSHER.get());

        this.tag(ItemTags.DYEABLE).add(ItemRegistry.ROBE_CHESTPLATE.get(), ItemRegistry.ROBE_LEGGINGS.get(), ItemRegistry.ROBE_BOOTS.get(),
                ItemRegistry.VOID_ROBE_HELMET.get(), ItemRegistry.VOID_ROBE_CHESTPLATE.get(), ItemRegistry.VOID_ROBE_LEGGINGS.get());
        this.tag(ModTags.SHOW_ASPECT_WHEN_EQUIPPED).add(ItemRegistry.GOGGLES.get());
    }
}