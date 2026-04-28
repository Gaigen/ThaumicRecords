package team.torka.thaumicrecords.api.research;

import net.minecraft.resources.ResourceLocation;

public class ResearchCategory {

    public final String key;

    public final ResourceLocation icon;

    public final ResourceLocation background;

    public ResearchCategory(String key, ResourceLocation icon, ResourceLocation background) {
        this.key = key;
        this.icon = icon;
        this.background = background;
    }
}
