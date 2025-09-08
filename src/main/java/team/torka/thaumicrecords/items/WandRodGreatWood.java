package team.torka.thaumicrecords.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.items.WandRod;

public class WandRodGreatWood extends WandRod {
    public WandRodGreatWood(Properties properties) {
        super(properties);
    }

    @Override
    public int getCraftCost() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 50;
    }

    @Override
    public void onUpdate(ItemStack itemstack, ServerPlayer player) {

    }
}
