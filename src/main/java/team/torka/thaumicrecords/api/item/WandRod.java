package team.torka.thaumicrecords.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;

public class WandRod {

    private final String name;

    private final int capacity;

    private final int craftCost;

    private final Item item;

    private final ResourceLocation modelTexture;

    public WandRod(String name, int capacity, int craftCost, Item item, ResourceLocation modelTexture) {
        this.name = name;
        this.capacity = capacity;
        this.craftCost = craftCost;
        this.item = item;
        this.modelTexture = modelTexture;
    }

    public WandRod(String name, int capacity, int craftCost, Item item) {
        this(name, capacity, craftCost, item, ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID,
                "textures/model/rod/" + name.toLowerCase() + ".png"));
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

    public Item getItem() {
        return item;
    }

    public ResourceLocation getModelTexture() {
        return modelTexture;
    }

    public void onUpdate(ItemStack itemstack, ServerPlayer player) {
    }

}
