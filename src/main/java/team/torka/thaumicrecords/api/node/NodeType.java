package team.torka.thaumicrecords.api.node;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;

public class NodeType {
    private final String translationKey;

    private final ResourceLocation nodeTexture;

    private final float renderSizeModifier;

    private final boolean rotate;

    private final RenderType renderType;

    private final int regenFrequency;

    public NodeType(String translationKey, ResourceLocation nodeTexture, RenderType renderType, int regenFrequency, float renderSizeModifier, boolean rotate) {
        this.translationKey = translationKey;
        this.nodeTexture = nodeTexture;
        this.renderSizeModifier = renderSizeModifier;
        this.rotate = rotate;
        this.renderType = renderType;
        this.regenFrequency = regenFrequency;
    }

    public NodeType(String translationKey, ResourceLocation nodeTexture, RenderType renderType) {
        this(translationKey, nodeTexture, renderType, 600, 1, false);
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
        ThaumicRecords.LOGGER.debug("randomTick");
    }

    public void onTick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
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

    public boolean isRotate() {
        return rotate;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public int getRegenFrequency() {
        return regenFrequency;
    }
}
