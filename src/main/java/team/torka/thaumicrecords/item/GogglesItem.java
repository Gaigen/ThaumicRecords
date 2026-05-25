package team.torka.thaumicrecords.item;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.torka.thaumicrecords.registry.ArmorMaterialRegistry;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GogglesItem extends ArmorItem {

    public GogglesItem() {
        super(ArmorMaterialRegistry.GOGGLES, Type.HELMET, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(350));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

}
