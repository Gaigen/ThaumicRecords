package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class WispEssenceItem extends Item implements IEssentiaContainerItem {
    public WispEssenceItem() {
        super(new Properties());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        AspectList aspects = getAspects(stack);
        if (!aspects.isEmpty()) {
            aspects.forEach((aspect, amount) -> {
                Aspect aspect1 = AspectRegistry.ASPECT_REGISTRY.get(aspect);
                if (!Objects.isNull(aspect1)) {
                    tooltip.add(Component.translatable(aspect1.getNameTranslationKey()).append(" x" + amount).withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "unknown_aspect")));
                }
            });
        }
    }

    public AspectList getAspects(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(AspectList.empty())).getAspects();
    }

    @Override
    public void setAspects(ItemStack stack, AspectList paramAspectList) {
        stack.set(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(paramAspectList));
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public int poursBy() {
        return 0;
    }

    @Override
    public int capacity() {
        return 2;
    }

    @Override
    public void onEmpty(ItemStack stack, Player player) {

    }

    @Override
    public void wasPoured(ItemStack stack, Player player, int amount) {

    }

    @Override
    public boolean canBePartiallyPoured() {
        return false;
    }

    @Override
    public boolean canHoldMultipleAspects() {
        return false;
    }

}
