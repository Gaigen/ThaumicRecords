package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;
import team.torka.thaumicrecords.client.model.JarModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;

public class JarBlockEntityRenderer implements BlockEntityRenderer<JarBlockEntity> {


    private final JarModel model;
    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/jar.png");

    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new JarModel(context.bakeLayer(CustomModelLayer.JAR));
    }

    @Override
    public void render(JarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        /*
         * Move model into block center
         */
        poseStack.translate(0.5D, 0.01D, 0.5D);

        /*
         * Vanilla model system is upside down for block entities
         */
        poseStack.scale(1.0F, -1.0F, 1.0F);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);

        poseStack.popPose();
    }
}
