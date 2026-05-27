package team.torka.thaumicrecords.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.IRunicArmor;
import team.torka.thaumicrecords.api.IVisDiscountGear;
import team.torka.thaumicrecords.api.IWarpingGear;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.List;

public class CultistBootsItem extends ArmorItem implements IRunicArmor, IWarpingGear, IVisDiscountGear {

    public CultistBootsItem(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return 1;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§5Vis Discount: " + getVisDiscount(stack, null, null) + "%"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}