package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import java.util.List;
import java.util.Objects;

public class Wand extends Item {

    public Wand(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    public WandRod getRod(ItemStack stack) {
        return WandRodRegistry.WAND_ROD_WOOD.get();
    }

    public WandCap getCap(ItemStack stack) {
        return WandCapRegistry.WAND_CAP_IRON.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data == null) {
            tooltip.add(Component.translatable("tooltip.thaumicrecords.wand.badComponent").withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip.thaumicrecords.rod", data.getRodKey()).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.thaumicrecords.cap", data.getCapKey()).withStyle(ChatFormatting.GOLD));

        AspectList aspects = data.getAspects();
        tooltip.add(Component.literal("aspects:").withStyle(ChatFormatting.DARK_PURPLE));
        aspects.forEach((resourceLocation, amount) -> {
            Aspect aspect1 = AspectRegistry.ASPECT_REGISTRY.get(resourceLocation);
            if (Objects.nonNull(aspect1)) {
                tooltip.add(Component.literal(" ")
                        .append(Component.translatable(aspect1.getName())) // 或者使用 resourceLocation.getName()
                        .append(Component.literal(": " + amount))
                        .withStyle(ChatFormatting.AQUA));
            }
        });

    }

    private boolean checkComponent(WandItemComponent component) {
        return true;
    }
}
