package team.torka.thaumicrecords.api.node;

import net.minecraft.resources.ResourceLocation;

public class NodeType {
    private final String translationKey;

    private final ResourceLocation nodeTexture;

    public NodeType(String translationKey, ResourceLocation nodeTexture) {
        this.translationKey = translationKey;
        this.nodeTexture = nodeTexture;
    }

    void onTick() {

    }

    public String getTranslationKey() {
        return translationKey;
    }

    public ResourceLocation getNodeTexture() {
        return nodeTexture;
    }

}
