package team.torka.thaumicrecords.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.client.ClientProxy;
import team.torka.thaumicrecords.registry.SoundRegistry;

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
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.PAGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
