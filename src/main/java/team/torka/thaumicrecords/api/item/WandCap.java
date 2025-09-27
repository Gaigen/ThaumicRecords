package team.torka.thaumicrecords.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WandCap {

    private final String name;

    private final int craftCost;

    private final Map<Aspect, Double> aspectCostModifier;

    private final Item item;

    private final ResourceLocation modelTexture;

    public WandCap(String name, Map<Aspect, Double> aspectCostModifier, int craftCost, Item item,
                   ResourceLocation modelTexture) {
        this.name = name;
        if (Objects.isNull(aspectCostModifier)) {
            this.aspectCostModifier = Collections.emptyMap();
        } else {
            for (var key : aspectCostModifier.keySet()) {
                if (!Aspect.getPrimal().contains(key)) {
                    throw new IllegalArgumentException(
                            "WandCap {" + name + "} aspectCostModifier should only apply to primal aspect but got {" +
                                    key.getName() + "}");
                }
            }
            this.aspectCostModifier = aspectCostModifier;
        }
        this.craftCost = craftCost;
        this.item = item;
        this.modelTexture = modelTexture;
    }

    public WandCap(String name, Map<Aspect, Double> aspectCostModifier, int craftCost, Item item) {
        this(name, aspectCostModifier, craftCost, item, ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID,
                "textures/model/" + name.toLowerCase() + ".png"));
    }

    public String getName() {
        return name;
    }

    public int getCraftCost() {
        return craftCost;
    }

    public double getAspectCostModifier(Aspect aspect) {
        return aspectCostModifier.getOrDefault(aspect, 1.0);
    }

    public Item getItem() {
        return item;
    }

    public ResourceLocation getModelTexture() {
        return modelTexture;
    }

    public static Map<Aspect, Double> getAllAspectModifierWithAmount(Double modifier) {
        var modifierMap = new HashMap<Aspect, Double>();
        for (var aspect : Aspect.getPrimal()) {
            modifierMap.put(aspect, modifier);
        }
        return modifierMap;
    }
}
