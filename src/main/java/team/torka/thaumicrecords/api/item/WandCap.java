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

    private final Map<ResourceLocation, Double> aspectCostModifier;

    private final Item item;

    private final ResourceLocation modelTexture;

    private final String translationKey;

    public WandCap(String name, Map<ResourceLocation, Double> aspectCostModifier, int craftCost, Item item, ResourceLocation modelTexture,
                   String translationKey) {
        this.name = name;
        if (Objects.isNull(aspectCostModifier)) {
            this.aspectCostModifier = Collections.emptyMap();
        } else {
            for (var key : aspectCostModifier.keySet()) {
                if (!Aspect.getPrimalList().contains(key)) {
                    throw new IllegalArgumentException("WandCap {" + name + "} aspectCostModifier should only apply to primal aspect but got {" + key + "}");
                }
            }
            this.aspectCostModifier = aspectCostModifier;
        }
        this.craftCost = craftCost;
        this.item = item;
        this.modelTexture = modelTexture;
        this.translationKey = translationKey;
    }

    /**
     * 内部使用
     */
    public WandCap(String name, Map<ResourceLocation, Double> aspectCostModifier, int craftCost, Item item) {
        this(name, aspectCostModifier, craftCost, item, ThaumicRecords.createRl("textures/item/cap/" + name.toLowerCase() + ".png"),
                ThaumicRecords.createTranslationKey("wand_cap", name));
    }

    public String getName() {
        return name;
    }

    public int getCraftCost() {
        return craftCost;
    }

    public double getAspectCostModifier(ResourceLocation aspect) {
        return aspectCostModifier.getOrDefault(aspect, 1.0);
    }

    public Item getItem() {
        return item;
    }

    public ResourceLocation getModelTexture() {
        return modelTexture;
    }

    public static Map<ResourceLocation, Double> getAllAspectModifierWithAmount(Double modifier) {
        var modifierMap = new HashMap<ResourceLocation, Double>();
        for (var aspect : Aspect.getPrimalList()) {
            modifierMap.put(aspect, modifier);
        }
        return modifierMap;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
