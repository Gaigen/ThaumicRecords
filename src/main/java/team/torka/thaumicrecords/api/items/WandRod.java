package team.torka.thaumicrecords.api.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class WandRod extends Item {

    public WandRod(Properties properties) {
        super(properties);
    }

    public abstract int getCraftCost();

    public abstract int getCapacity();

    public abstract void onUpdate(ItemStack itemstack, ServerPlayer player);

}
