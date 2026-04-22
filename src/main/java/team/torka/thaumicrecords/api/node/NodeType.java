package team.torka.thaumicrecords.api.node;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class NodeType {
    private final String translationKey;

    private final ResourceLocation nodeTexture;

    private final float renderSizeModifier;

    private final float renderAngleModifier;

    private final RenderType renderType;

    public NodeType(String translationKey, ResourceLocation nodeTexture, float renderSizeModifier, float renderAngleModifier, RenderType renderType) {
        this.translationKey = translationKey;
        this.nodeTexture = nodeTexture;
        this.renderSizeModifier = renderSizeModifier;
        this.renderAngleModifier = renderAngleModifier;
        this.renderType = renderType;
    }

    void onTick() {

    }

    public String getTranslationKey() {
        return translationKey;
    }

    public ResourceLocation getNodeTexture() {
        return nodeTexture;
    }

    public float getRenderSizeModifier() {
        return renderSizeModifier;
    }

    public float getRenderAngleModifier() {
        return renderAngleModifier;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}
