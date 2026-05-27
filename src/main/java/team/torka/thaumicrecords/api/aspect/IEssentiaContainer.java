package team.torka.thaumicrecords.api.aspect;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IEssentiaContainer {
    //if true - aspect amount may change, false - aspect amount is constant for that item, like phials do
    boolean isVariable();

    //pials is containing liquid essense, when the wisp essense or crystalized essence - not
    boolean isLiquid();

    //how much essentia are poured by one time, 0 for non-liquid
    int poursBy();

    int capacity();

    void onEmpty(ItemStack stack, Player player);

    void wasPoured(ItemStack stack, Player player, int amount);

    boolean canBePartiallyPoured();

    boolean canHoldMultipleAspects();


}
