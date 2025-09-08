package team.torka.thaumicrecords.items;

import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.items.WandCap;

import java.util.HashMap;
import java.util.Map;

public class WandCapIron extends WandCap {
    public WandCapIron(Properties properties) {
        super(properties);
    }

    @Override
    public int getCraftCost() {
        return 0;
    }

    @Override
    public Map<Aspect, Double> aspectCodeModifier() {
        return new HashMap<>();
    }
}
