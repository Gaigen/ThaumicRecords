package team.torka.thaumicrecords.registry;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

import java.util.EnumMap;
import java.util.List;

public class ArmorMaterialRegistry {
    public static final DeferredRegister<ArmorMaterial> REGISTRAR = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, ThaumicRecords.MOD_ID);


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPECIAL = REGISTRAR.register("special",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.BOOTS, 1);
            }), 25, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(net.minecraft.world.item.Items.GOLD_INGOT),
                    List.of(new ArmorMaterial.Layer(ThaumicRecords.createRl("special"))), 0.0F, 0.0F));
}
