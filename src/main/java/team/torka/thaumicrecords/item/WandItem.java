package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class WandItem extends Item {

    public WandItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data == null) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "bad_component")).withStyle(ChatFormatting.GRAY));
            return;
        }
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
        WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
        if (Objects.isNull(wandRod) || Objects.isNull(wandCap)) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "bad_component")).withStyle(ChatFormatting.GRAY));
            return;
        }
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.capacity"), wandRod.getCapacityScaled())
                    .withStyle(ChatFormatting.GOLD));
            for (ResourceLocation rl : Aspect.getPrimalList()) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.isNull(aspect)) {
                    continue;
                }
                String scaled = data.getAspects().getScaled(rl);
                BigDecimal modifier = BigDecimal.valueOf(wandCap.getAspectCostModifier(rl)).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
                MutableComponent line = Component.literal(" ")
                        .append(Component.translatable(aspect.getNameTranslationKey()).withStyle(aspect.getTextColor()))
                        .append(Component.literal(" x ")
                                .withStyle(ChatFormatting.WHITE)
                                .append(Component.literal(scaled))
                                .append(" ")
                                .append(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.modifier"), modifier)
                                        .withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC)));
                tooltip.add(line);
            }
        } else {
            BigDecimal averageModifier = BigDecimal.valueOf(Aspect.getPrimalList().stream().map(wandCap::getAspectCostModifier).reduce(0.0, Double::sum))
                    .divide(BigDecimal.valueOf(Aspect.getPrimalList().size()))
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP);
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.capacity"), wandRod.getCapacityScaled())
                    .withStyle(ChatFormatting.GOLD)
                    .append(" ")
                    .append(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.average_modifier"), averageModifier.toPlainString())
                            .withStyle(ChatFormatting.WHITE)));
            MutableComponent line = Component.literal("");
            Iterator<ResourceLocation> iterator = Aspect.getPrimalList().iterator();
            while (iterator.hasNext()) {
                ResourceLocation rl = iterator.next();
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.isNull(aspect)) {
                    continue;
                }
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
                return Component.translatable(ThaumicRecords.createTranslationKey("item", "wand"), capPart, rodPart);
            }
        }
        return Component.translatable(ThaumicRecords.createTranslationKey("item", "wand.default"));
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 72000;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof ServerPlayer player) {
            HitResult hitResult = player.pick(5.0D, 0.0F, true);
            if (hitResult instanceof BlockHitResult blockHit && level.getBlockEntity(blockHit.getBlockPos()) instanceof AuraNodeBlockEntity nodeBE) {
                if (!stack.is(ItemRegistry.WAND)) {
                    player.stopUsingItem();
                    return;
                }
                WandItemComponent wandItemComponent = stack.get(DataComponentRegistry.WAND_ITEM_DATA);
                if (Objects.isNull(wandItemComponent)) {
                    player.stopUsingItem();
                    return;
                }
                int useDuration = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
                if (useDuration % 5 == 0) {
                    int drainRate = 1;
                    // TODO 研究增加吸取速率
                    boolean preserve = !player.isShiftKeyDown();
                    // TODO 节点防护术
                    // TODO 铁杖端木杖柄判断
                    List<ResourceLocation> notFull = wandItemComponent.getLackVisAspect();
                    List<ResourceLocation> randomPrimalList = nodeBE.getLimitAspect().getPrimalKey().stream().filter(notFull::contains).toList();
                    if (!randomPrimalList.isEmpty()) {
                        ResourceLocation randomAspect = randomPrimalList.get(level.random.nextInt(randomPrimalList.size()));
                        int space = wandItemComponent.getCapacity() - wandItemComponent.getAspects().get(randomAspect);
                        int toDrain = Math.min(drainRate, space);
                        int drained = nodeBE.drainAspect(randomAspect, toDrain, preserve);
                        if (drained > 0) {
                            AtomicInteger remain = new AtomicInteger();
                            WandItemComponent newComponent = wandItemComponent.addVis(randomAspect, drained, remain);
                            if (remain.get() < drained) {
                                nodeBE.getCurrentAspect().add(randomAspect, remain.get());
                                stack.set(DataComponentRegistry.WAND_ITEM_DATA, newComponent);
                            }
                        }
                    }
                }
            } else {
                player.stopUsingItem();
            }
        }
    }

}
