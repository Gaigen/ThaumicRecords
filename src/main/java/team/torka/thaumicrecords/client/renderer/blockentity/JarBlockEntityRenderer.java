package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;
import team.torka.thaumicrecords.client.model.JarModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;

public class JarBlockEntityRenderer implements BlockEntityRenderer<JarBlockEntity> {
    private final JarModel model;
    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/jar.png");
    private static final ResourceLocation BRINE_TEXTURE = ThaumicRecords.createRl("textures/block/animated_glow.png");


    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new JarModel(context.bakeLayer(CustomModelLayer.JAR));
    }

    @Override
    public void render(JarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.01D, 0.5D);
        poseStack.scale(1.0F, -1.0F, 1.0F);

        // this need to be rendered first cuz some blending trash causes
        // that something rendering inside AFTER the outer jar body would rendered - dissapears
        if (!blockEntity.getAspects().isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0D, -0.375D, 0D);
            poseStack.scale(0.6F, -.65F, 0.6F);

            long gameTime = Minecraft.getInstance().level.getGameTime();
            int totalFrames = 20;
            float speed = 0.25F;
            int frame = (int) (gameTime % (totalFrames * speed));
            float v0 = (float) frame / totalFrames;
            float v1 = (float) (frame + 1) / totalFrames;

            float fillPercent = ((float) blockEntity.storedAmount()) / ((float) blockEntity.capacity());

            Aspect aspect = blockEntity.getStoredAspect();
            int color = aspect.getARGBColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            renderCube(poseStack, bufferSource, packedLight, packedOverlay, v0, v1, fillPercent, r, g, b, 0.8f);
            poseStack.popPose();
        }

        // Рендер тела и крышки (всегда)
        VertexConsumer glassConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
//        VertexConsumer glassConsumer = bufferSource.getBuffer(CustomRenderType.entit(TEXTURE));
        model.getBody().render(poseStack, glassConsumer, packedLight, packedOverlay);
        model.getLid().render(poseStack, glassConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderCube(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float v0, float v1, float fillPercent,
                            float r, float g, float b, float a) {
        if (fillPercent <= 0) {
            return;
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(BRINE_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float yMin = -0.5f;
        float yMax = yMin + fillPercent; // максимум 0.5
        float xMin = -0.45f, xMax = 0.45f;
        float zMin = -0.45f, zMax = 0.45f;

        // Передняя грань (z = zMax)
        consumer.addVertex(matrix, xMin, yMin, zMax).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                1f);
        consumer.addVertex(matrix, xMax, yMin, zMax).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                1f);
        consumer.addVertex(matrix, xMax, yMax, zMax).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                1f);
        consumer.addVertex(matrix, xMin, yMax, zMax).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                1f);

        // Задняя грань (z = zMin)
        consumer.addVertex(matrix, xMin, yMin, zMin).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                -1f);
        consumer.addVertex(matrix, xMin, yMax, zMin).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                -1f);
        consumer.addVertex(matrix, xMax, yMax, zMin).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                -1f);
        consumer.addVertex(matrix, xMax, yMin, zMin).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, 0f,
                -1f);

        // Левая грань (x = xMin)
        consumer.addVertex(matrix, xMin, yMin, zMin).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, -1f, 0f,
                0f);
        consumer.addVertex(matrix, xMin, yMin, zMax).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, -1f, 0f,
                0f);
        consumer.addVertex(matrix, xMin, yMax, zMax).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, -1f, 0f,
                0f);
        consumer.addVertex(matrix, xMin, yMax, zMin).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, -1f, 0f,
                0f);

        // Правая грань (x = xMax)
        consumer.addVertex(matrix, xMax, yMin, zMax).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 1f, 0f,
                0f);
        consumer.addVertex(matrix, xMax, yMin, zMin).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 1f, 0f,
                0f);
        consumer.addVertex(matrix, xMax, yMax, zMin).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 1f, 0f,
                0f);
        consumer.addVertex(matrix, xMax, yMax, zMax).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 1f, 0f,
                0f);

        // Нижняя грань (y = yMin) – только если yMin > -0.5 (всегда)
        consumer.addVertex(matrix, xMin, yMin, zMin).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, -1f,
                0f);
        consumer.addVertex(matrix, xMax, yMin, zMin).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, -1f,
                0f);
        consumer.addVertex(matrix, xMax, yMin, zMax).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, -1f,
                0f);
        consumer.addVertex(matrix, xMin, yMin, zMax).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f, -1f,
                0f);

        // Верхняя грань (y = yMax) – рисуем только если yMax > yMin
        if (yMax > yMin + 0.001f) {
            consumer.addVertex(matrix, xMin, yMax, zMax).setColor(r, g, b, a).setUv(0f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f,
                    1f, 0f);
            consumer.addVertex(matrix, xMax, yMax, zMax).setColor(r, g, b, a).setUv(1f, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f,
                    1f, 0f);
            consumer.addVertex(matrix, xMax, yMax, zMin).setColor(r, g, b, a).setUv(1f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f,
                    1f, 0f);
            consumer.addVertex(matrix, xMin, yMax, zMin).setColor(r, g, b, a).setUv(0f, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0f,
                    1f, 0f);
        }
    }

}
