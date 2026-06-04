package team.torka.thaumicrecords.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import team.torka.thaumicrecords.ThaumicRecords;

public class StaffRod extends WandRod {

    private boolean runes = false;

    public StaffRod(String name, int capacity, int craftCost, Item item, ResourceLocation modelTexture, String translationKey) {
        super(name + "_staff", capacity, craftCost, item, modelTexture, translationKey);
    }

    public StaffRod(String name, int capacity, int craftCost, Item item) {
        super(name + "_staff", capacity, craftCost, item, ThaumicRecords.createRl("textures/item/rod/" + name.toLowerCase() + ".png"),
                ThaumicRecords.createTranslationKey("staff_rod", name));
    }

    public boolean hasRunes() {
        return this.runes;
    }

    public void setRunes(boolean hasRunes) {
        this.runes = hasRunes;
    }
}
