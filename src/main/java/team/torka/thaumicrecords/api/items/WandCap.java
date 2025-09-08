package team.torka.thaumicrecords.api.items;

import net.minecraft.world.item.Item;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.Map;

public abstract class WandCap extends Item {

    public WandCap(Item.Properties properties) {
        super(properties);
    }

    public abstract int getCraftCost();

    public abstract Map<Aspect, Double> aspectCodeModifier();
}
