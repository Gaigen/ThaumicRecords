package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.item.StaffRod;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.block.ThaumatoriumBlock;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.block.entity.CrucibleBlockEntity;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;
import team.torka.thaumicrecords.block.entity.ThaumatoriumBlockEntity;
import team.torka.thaumicrecords.block.part.ThaumatoriumPart;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.entity.SpecialItemEntity;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.EntityRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;
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

    public WandItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (Objects.isNull(data)) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "bad_component")).withStyle(ChatFormatting.GRAY));
            return;
        }
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
        WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
        if (Objects.isNull(wandRod) || Objects.isNull(wandCap)) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "bad_component")).withStyle(ChatFormatting.GRAY));
            return;
        }
        String capacityScaled = BigDecimal.valueOf(data.getEffectiveCapacity()).divide(new BigDecimal(100), RoundingMode.HALF_UP).setScale(2,
                RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.capacity"), capacityScaled).withStyle(ChatFormatting.GOLD));
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
                    .divide(BigDecimal.valueOf(Aspect.getPrimalList().size()), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP);
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "wand.capacity"), capacityScaled)
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
                String key = data.sceptre() ? "item.wand.sceptre" : (isStaff(stack) ? "item.wand.staff" : "item.wand");
                return Component.translatable(ThaumicRecords.createTranslationKey("item", key), capPart, rodPart);
            }
        }
        return Component.translatable(ThaumicRecords.createTranslationKey("item", "wand.default"));
    }

    public boolean isStaff(ItemStack stack) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data == null) {
            return false;
        }
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
        return wandRod instanceof StaffRod;
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
    @NotNull
    @ParametersAreNonnullByDefault
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.CAULDRON)) {
            if (!level.isClientSide) {
                BlockState crucibleState = BlockRegistry.CRUCIBLE.get().defaultBlockState();
                level.setBlock(pos, crucibleState, 3);
                level.playSound(null, pos, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (state.is(BlockRegistry.CRUCIBLE.get())) {
            if (player.isShiftKeyDown() && !level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
                    crucible.spillRemnants();
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (state.is(BlockRegistry.ALCHEMICAL_CONSTRUCT.get())) {
            BlockPos posBelow = pos.below();
            BlockState stateBelow = level.getBlockState(posBelow);
            BlockPos posAbove = pos.above();
            BlockState stateAbove = level.getBlockState(posAbove);
            BlockPos cruciblePos = posBelow.below();
            BlockState crucibleState = level.getBlockState(cruciblePos);

            BlockPos bottomPos = null;
            BlockPos topPos = null;

            if (stateBelow.is(BlockRegistry.ALCHEMICAL_CONSTRUCT.get()) && crucibleState.is(BlockRegistry.CRUCIBLE.get())) {
                bottomPos = posBelow;
                topPos = pos;
            } else if (stateAbove.is(BlockRegistry.ALCHEMICAL_CONSTRUCT.get()) && stateBelow.is(BlockRegistry.CRUCIBLE.get())) {
                bottomPos = pos;
                topPos = posAbove;
            }

            if (bottomPos != null && topPos != null) {
                if (!level.isClientSide) {
                    level.setBlock(bottomPos, BlockRegistry.THAUMATORIUM.get().defaultBlockState().setValue(ThaumatoriumBlock.PART, ThaumatoriumPart.BOTTOM),
                            3);
                    level.setBlock(topPos, BlockRegistry.THAUMATORIUM.get().defaultBlockState().setValue(ThaumatoriumBlock.PART, ThaumatoriumPart.TOP), 3);

                    if (level.getBlockEntity(bottomPos) instanceof ThaumatoriumBlockEntity thaum) {
                        thaum.facing = player.getDirection().getOpposite();
                        thaum.syncToClient();
                        thaum.setChanged();
                    }

                    level.playSound(null, topPos, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (state.is(BlockRegistry.INFUSION_MATRIX.get())) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof InfusionMatrixBlockEntity matrix) {
                    if (!matrix.active) {
                        if (matrix.checkStructure()) {
                            matrix.activate();
                        } else {
                            level.playSound(null, pos, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.25F, 0.5F);
                        }
                    } else if (!matrix.crafting) {
                        // Matrix is active and not crafting — try to find and start recipe
                        var recipe = matrix.findMatchingRecipe();
                        if (recipe != null) {
                            matrix.startInfusion(recipe);
                        } else {
                            level.playSound(null, pos, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.25F, 0.5F);
                        }
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (state.is(Tags.Blocks.BOOKSHELVES)) {
            level.removeBlock(pos, false);
            ItemStack itemStack = new ItemStack(ItemRegistry.THAUMONOMICON.get(), 1);
            ItemEntity entityItem = new SpecialItemEntity(EntityRegistry.SPECIAL_ITEM.get(), level, pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                    itemStack);
            entityItem.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(entityItem);
            level.playSound(null, pos, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
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
                    boolean preserve = !player.isShiftKeyDown();
                    List<ResourceLocation> notFull = wandItemComponent.getLackVisAspect();
                    List<ResourceLocation> randomPrimalList = nodeBE.getLimitAspect().getPrimalKey().stream().filter(notFull::contains).toList();
                    if (!randomPrimalList.isEmpty()) {
                        ResourceLocation randomAspect = randomPrimalList.get(level.random.nextInt(randomPrimalList.size()));
                        int space = wandItemComponent.getEffectiveCapacity() - wandItemComponent.getAspects().getOrZero(randomAspect);
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
