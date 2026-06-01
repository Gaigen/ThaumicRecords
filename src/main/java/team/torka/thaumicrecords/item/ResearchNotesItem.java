package team.torka.thaumicrecords.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class ResearchNotesItem extends Item {
    public ResearchNotesItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
            if (Objects.nonNull(data) && data.complete()) {
                ResourceLocation researchKey = data.research();
                ResearchHelper.completeResearch(serverPlayer, researchKey);
                stack.consume(1, player);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
        if (Objects.isNull(data)) {
            return;
        }

        Research research = ResearchRegistry.RESEARCH_REGISTRY.get(data.research());
        if (Objects.isNull(research)) {
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.unknown_research")).withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable(research.nameTranslationKey).withColor(0xFFAA00));
        tooltip.add(Component.translatable(research.descTranslationKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        if (research.warp > 0) {
            int level = Math.min(research.warp, 5);
            tooltip.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.forbidden"),
                    Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.forbidden.level." + level))).withColor(0xAA00AA));
        }
    }
}
