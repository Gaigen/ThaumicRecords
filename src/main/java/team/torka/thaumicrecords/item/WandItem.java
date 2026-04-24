package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class WandItem extends Item {

    public WandItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data == null) {
            tooltip.add(Component.translatable("tooltip.thaumicrecords.wand.bad_component").withStyle(ChatFormatting.GRAY));
            return;
        }
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
        WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
        if (Objects.isNull(wandRod) || Objects.isNull(wandCap)) {
            tooltip.add(Component.translatable("tooltip.thaumicrecords.wand.bad_component").withStyle(ChatFormatting.GRAY));
            return;
        }
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.thaumicrecords.wand.capacity", wandRod.getCapacityScaled()).withStyle(ChatFormatting.GOLD));
            Iterator<Aspect> iterator = Aspect.getPrimal().iterator();
            while (iterator.hasNext()) {
                Aspect aspect = iterator.next();
                ResourceLocation rl = AspectRegistry.ASPECT_REGISTRY.getKey(aspect);
                String scaled = data.getAspects().getScaled(rl);
                BigDecimal modifier = BigDecimal.valueOf(wandCap.getAspectCostModifier(aspect)).multiply(BigDecimal.valueOf(100)).setScale(0,
                        RoundingMode.HALF_UP);
                MutableComponent line = Component.literal(" ")
                        .append(Component.translatable(aspect.getNameTranslationKey()).withStyle(aspect.getTextColor()))
                        .append(Component.literal(" x ")
                                .withStyle(ChatFormatting.WHITE)
                                .append(Component.literal(scaled))
                                .append(" ")
                                .append(Component.translatable("tooltip.thaumicrecords.wand.modifier", modifier)
                                        .withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC)));
                tooltip.add(line);
            }
        } else {
            BigDecimal averageModifier = BigDecimal.valueOf(Aspect.getPrimal().stream().map(wandCap::getAspectCostModifier).reduce(0.0, Double::sum)).divide(
                    BigDecimal.valueOf(Aspect.getPrimal().size())).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
            tooltip.add(Component.translatable("tooltip.thaumicrecords.wand.capacity", wandRod.getCapacityScaled())
                    .withStyle(ChatFormatting.GOLD)
                    .append(" ")
                    .append(Component.translatable("tooltip.thaumicrecords.wand.average_modifier", averageModifier.toPlainString())
                            .withStyle(ChatFormatting.WHITE)));
            MutableComponent line = Component.literal("");
            Iterator<Aspect> iterator = Aspect.getPrimal().iterator();
            while (iterator.hasNext()) {
                Aspect aspect = iterator.next();
                ResourceLocation rl = AspectRegistry.ASPECT_REGISTRY.getKey(aspect);
                line.append(Component.literal(String.valueOf(data.getAspects().getScaled(rl))).withStyle(aspect.getTextColor()));
                if (iterator.hasNext()) {
                    line.append(" | ");
                }
            }
            tooltip.add(line);
        }
    }

    @Override
    @NotNull
    public Component getName(ItemStack stack) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (Objects.nonNull(data)) {
            WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
            WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
            if (Objects.nonNull(wandRod) && Objects.nonNull(wandCap)) {
                Component capPart = Component.translatable(wandCap.getTranslationKey());
                Component rodPart = Component.translatable(wandRod.getTranslationKey());
                return Component.translatable("item.thaumicrecords.wand", capPart, rodPart);
            }
        }
        return Component.translatable("item.thaumicrecords.wand.default");
    }
}
