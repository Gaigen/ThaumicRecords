package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.ResearchTableBlock;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.block.part.ResearchTablePart;
import team.torka.thaumicrecords.client.model.ResearchTableModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;
import team.torka.thaumicrecords.menu.ResearchTableMenu;

import javax.annotation.ParametersAreNonnullByDefault;

public class ResearchTableBlockEntityRenderer implements BlockEntityRenderer<ResearchTableBlockEntity> {

    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/research_table.png");
    private static final ResourceLocation SCROLL = ThaumicRecords.createRl("textures/block/research_table_scroll.png");
    private static final ResourceLocation PARCHMENT = ThaumicRecords.createRl("textures/misc/parchment.png");
    private static final ResourceLocation QUILL = ThaumicRecords.createRl("textures/misc/quill.png");
    private final ResearchTableModel model;

    public ResearchTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ResearchTableModel(context.bakeLayer(CustomModelLayer.RESEARCH_TABLE));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ResearchTableBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        if (be.getBlockState().getValue(ResearchTableBlock.PART) != ResearchTablePart.LEFT) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float rotation = be.getBlockState().getValue(ResearchTableBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderTable(poseStack, vc, packedLight, packedOverlay);
        ItemStack scribeTools = be.getInventory().getStackInSlot(ResearchTableMenu.SLOT_SCRIBE_TOOLS);
        if (!scribeTools.isEmpty()) {
            model.renderInkwell(poseStack, vc, packedLight, packedOverlay);
            renderQuill(poseStack, bufferSource, packedLight, packedOverlay);
        }
        renderParchmentStack(poseStack, bufferSource, packedLight, packedOverlay);
        ItemStack researchNote = be.getInventory().getStackInSlot(ResearchTableMenu.SLOT_RESEARCH_NOTE);
        if (!researchNote.isEmpty()) {
            renderScroll(poseStack, bufferSource, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRenderOffScreen(ResearchTableBlockEntity be) {
        return true;
    }

    private void renderScroll(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer scrollVC = buffer.getBuffer(RenderType.entityCutout(SCROLL));
        int color = 0xFF999999;
        poseStack.translate(1, -0.1, -0.05);
        model.renderScroll(poseStack, scrollVC, light, overlay, color);
    }

    private void renderQuill(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer quillVC = buffer.getBuffer(RenderType.entityCutout(QUILL));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(0.08D, 0.15D, 0.23D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-15.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        renderSimpleQuad(quillVC, poseStack, light, overlay);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        RenderSystem.disableCull();
        poseStack.scale(-1F, 1F, 1F);
        renderSimpleQuad(quillVC, poseStack, light, overlay);
        RenderSystem.disableCull();
        poseStack.popPose();
    }

    private void renderParchmentStack(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer parchmentVC = buffer.getBuffer(RenderType.entityCutout(PARCHMENT));
        for (int a = 0; a < 6; ++a) {
            poseStack.pushPose();
            poseStack.translate(0.3D, 0.49D - (a * 0.015D), 0D);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(15.0F + (a % 3 * 2.0F)));
            poseStack.scale(0.5F, 0.6F, 0.6F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            renderSimpleQuad(parchmentVC, poseStack, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderSimpleQuad(VertexConsumer buffer, PoseStack poseStack, int light, int overlay) {
        PoseStack.Pose entry = poseStack.last();
        addVertex(buffer, entry, -0.5f, -0.5f, 0, 0, 1, light, overlay);
        addVertex(buffer, entry, 0.5f, -0.5f, 0, 1, 1, light, overlay);
        addVertex(buffer, entry, 0.5f, 0.5f, 0, 1, 0, light, overlay);
        addVertex(buffer, entry, -0.5f, 0.5f, 0, 0, 0, light, overlay);
    }

    private void addVertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int light, int overlay) {
        buffer.addVertex(pose, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 1.0F, 1.0F);
    }
}