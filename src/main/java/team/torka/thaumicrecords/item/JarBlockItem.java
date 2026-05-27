package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.block.JarBlock;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class JarBlockItem extends BlockItem implements IEssentiaContainerItem {


    public JarBlockItem(Block block, Properties properties) {
        super(block, properties);
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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        // Prevent placing jar when using on another jar without shift pressed, cuz we have behavior of pouring from one jar to another with click
        if (level.getBlockState(clickedPos).getBlock() instanceof JarBlock && player != null && !player.isShiftKeyDown()) {
            // Не разрешаем установку
            return InteractionResult.FAIL;
        }

        // Иначе – стандартная установка (вызов super)
        return super.useOn(context);
    }

    @Override
    public AspectList getAspects(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(AspectList.empty())).getAspects();
    }

    @Override
    public void setAspects(ItemStack stack, AspectList paramAspectList) {
        stack.set(DataComponentRegistry.ASPECT_LIST.get(), new AspectListComponent(paramAspectList));
    }

    @Override
    public @Nullable Aspect getStoredAspect(ItemStack paramItemStack) {
        if (paramItemStack.getItem() instanceof JarBlockItem item) {
            AspectList list = getAspects(paramItemStack);
            if (list.isEmpty()) {
                return null;
            }
            return AspectRegistry.ASPECT_REGISTRY.get(getAspects(paramItemStack).firstEntry().getKey());
        }
        return null;
    }

    @Override
    public @Nullable ResourceLocation getStoredAspectResource(ItemStack paramItemStack) {
        if (paramItemStack.getItem() instanceof JarBlockItem item) {
            AspectList list = getAspects(paramItemStack);
            if (list.isEmpty()) {
                return null;
            }
            return getAspects(paramItemStack).firstEntry().getKey();
        }
        return null;
    }

    @Override
    public int storedAmount(ItemStack paramItemStack) {
        if (paramItemStack.getItem() instanceof JarBlockItem item) {
            AspectList list = getAspects(paramItemStack);
            if (list.isEmpty()) {
                return 0;
            }
            return list.get(getAspects(paramItemStack).firstEntry().getKey());
        }
        return 0;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isLiquid() {
        return true;
    }

    @Override
    public int poursBy() {
        return 64;
    }

    @Override
    public int capacity() {
        return 64;
    }

    @Override
    public void onEmpty(ItemStack stack, Player player) {
        if (stack.getItem() instanceof JarBlockItem) {
            stack.shrink(1);
            ItemStack emptyJar = ItemRegistry.JAR.toStack();
            emptyJar.setCount(1);
            if (!player.getInventory().add(emptyJar)) {
                player.drop(emptyJar, false);
            }
        }
    }

    @Override
    public void wasPoured(ItemStack stack, Player player, int amount) {
        if (stack.getItem() instanceof JarBlockItem item) {
            AspectList aspectList = item.getAspects(stack).copy();
            ResourceLocation aspectResource = item.getStoredAspectResource(stack);
            int storedAmount = item.storedAmount(stack);
            if (storedAmount - amount < 0) {
                return; //todo: some sort of error propagation
            }
            if (storedAmount == amount) {
                onEmpty(stack, player);
                return;
            }
            int jarsInStack = stack.getCount();
            if (jarsInStack == 1) {
                aspectList.put(aspectResource, storedAmount - amount);
                item.setAspects(stack, aspectList);
                return;
            } else {
                stack.shrink(1);

                ItemStack emptyJar = ItemRegistry.JAR.toStack();
                emptyJar.setCount(1);
                aspectList.put(aspectResource, storedAmount - amount);
                item.setAspects(emptyJar, aspectList);
                if (!player.getInventory().add(emptyJar)) {
                    player.drop(emptyJar, false);
                }
                return;
            }
        }
    }

    @Override
    public boolean canBePartiallyPoured() {
        return true;
    }

    @Override
    public boolean canHoldMultipleAspects() {
        return false;
    }
}
