package team.torka.thaumicrecords.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class CrimsonRitesItem extends Item {

    public CrimsonRitesItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!ResearchHelper.isResearchCompleted(serverPlayer, ResearchRegistry.CRIMSON.getId())) {
                ResearchHelper.completeResearch(serverPlayer, ResearchRegistry.CRIMSON.getId());
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
