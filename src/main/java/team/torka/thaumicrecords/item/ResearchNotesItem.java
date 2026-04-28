package team.torka.thaumicrecords.item;

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
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

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
        if (!level.isClientSide && player instanceof ServerPlayer) {
            ResearchNoteComponent data = stack.get(DataComponentRegistry.RESEARCH_NOTE.get());
            if (data != null && data.complete()) {
                ResourceLocation researchKey = data.research();
                // TODO 研究完成
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
        // TODO 显示金色研究名称 灰色斜体研究描述，扭曲等级
    }
}
