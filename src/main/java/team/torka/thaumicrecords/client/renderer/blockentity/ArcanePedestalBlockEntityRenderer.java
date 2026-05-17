package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import team.torka.thaumicrecords.block.entity.ArcanePedestalBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcanePedestalBlockEntityRenderer implements BlockEntityRenderer<ArcanePedestalBlockEntity> {

    private final ItemRenderer itemRenderer;

    public ArcanePedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ArcanePedestalBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);

        poseStack.popPose();
    }
}
