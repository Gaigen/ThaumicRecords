package team.torka.thaumicrecords.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WandRod {

    private final String name;

    /**
     * 两位小数，实际显示是1/100，比如传入2500，那么显示是200
     */
    private final int capacity;

    private final int craftCost;

    private final Item item;

    private final ResourceLocation modelTexture;

    private final String translationKey;

    public WandRod(String name, int capacity, int craftCost, Item item, ResourceLocation modelTexture, String translationKey) {
        this.name = name;
        this.capacity = capacity;
        this.craftCost = craftCost;
        this.item = item;
        this.modelTexture = modelTexture;
        this.translationKey = translationKey;
    }

    /**
     * 内部使用
     */
    public WandRod(String name, int capacity, int craftCost, Item item) {
        this(name, capacity, craftCost, item, ThaumicRecords.createRl("textures/item/rod/" + name.toLowerCase() + ".png"),
                ThaumicRecords.createTranslationKey("wand_rod", name));
    }

    public String getName() {
        return name;
    }

    public int getCraftCost() {
        return craftCost;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getCapacityScaled() {
        return BigDecimal.valueOf(capacity)
                .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public Item getItem() {
        return item;
    }

    public ResourceLocation getModelTexture() {
        return modelTexture;
    }

    public void onUpdate(ItemStack itemstack, ServerPlayer player) {
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
