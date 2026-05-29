package team.torka.thaumicrecords.api.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;

import javax.annotation.Nullable;

public class ResearchCategory {
    public static final ResourceLocation DEFAULT_BGTEX = ThaumicRecords.createRl("textures/research/background/researchback.png");

    public final String nameTranslateKey;

    /**
     * 图标
     */
    @Nullable
    public final ResourceLocation icon;

    /**
     * 图标，优先级大于icon
     */
    @Nullable
    public final ItemStack iconItem;

    public final ResourceLocation background;

    public ResearchCategory(String nameTranslateKey, @Nullable ResourceLocation icon, @Nullable ItemStack iconItem, ResourceLocation background) {
        this.nameTranslateKey = nameTranslateKey;
        this.icon = icon;
        this.iconItem = iconItem;
        this.background = background;
    }
}
