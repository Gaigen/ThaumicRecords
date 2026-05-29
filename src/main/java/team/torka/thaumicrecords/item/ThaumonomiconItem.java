package team.torka.thaumicrecords.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.client.ClientProxy;

import javax.annotation.ParametersAreNonnullByDefault;

public class ThaumonomiconItem extends Item {
    public ThaumonomiconItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            ClientProxy.openThaumonomiconScreen();
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
