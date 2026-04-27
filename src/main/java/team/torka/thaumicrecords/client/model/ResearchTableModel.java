package team.torka.thaumicrecords.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

public class ResearchTableModel extends Model {
    private final ModelPart root;
    private final ModelPart inkwell;
    private final ModelPart scrollTube;
    private final ModelPart scrollRibbon;

    public ResearchTableModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.inkwell = root.getChild("Inkwell");
        this.scrollTube = root.getChild("ScrollTube");
        this.scrollRibbon = root.getChild("ScrollRibbon");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -8.0F, 32.0F, 4.0F, 16.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        partdefinition.addOrReplaceChild("Leg1", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-6.0F, 12.0F, -6.0F));
        partdefinition.addOrReplaceChild("Leg2", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-6.0F, 12.0F, 2.0F));
        partdefinition.addOrReplaceChild("Leg3", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(18.0F, 12.0F, -6.0F));
        partdefinition.addOrReplaceChild("Leg4", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(18.0F, 12.0F, 2.0F));
        partdefinition.addOrReplaceChild("Crossbar", CubeListBuilder.create().texOffs(24, 24).addBox(0.0F, 0.0F, 0.0F, 24.0F, 4.0F, 4.0F),
                PartPose.offset(-4.0F, 18.0F, -2.0F));
        partdefinition.addOrReplaceChild("Inkwell", CubeListBuilder.create().texOffs(0, 44).addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 3.0F),
                PartPose.offset(-6.0F, 6.0F, 3.0F));
        partdefinition.addOrReplaceChild("ScrollTube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 8.5F, 0.0F, 0.0F, 0.25F * Mth.PI, 0.0F));
        partdefinition.addOrReplaceChild("ScrollRibbon", CubeListBuilder.create().texOffs(0, 4).addBox(1.5F, -1.1F, -1.1F, 1.0F, 2F, 2F),
                PartPose.offsetAndRotation(0.0F, 8.5F, 0.0F, 0.0F, 0.25F * Mth.PI, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public void renderTable(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
        root.getChild("Top").render(poseStack, buffer, light, overlay);
        root.getChild("Leg1").render(poseStack, buffer, light, overlay);
        root.getChild("Leg2").render(poseStack, buffer, light, overlay);
        root.getChild("Leg3").render(poseStack, buffer, light, overlay);
        root.getChild("Leg4").render(poseStack, buffer, light, overlay);
        root.getChild("Crossbar").render(poseStack, buffer, light, overlay);
    }

    public void renderInkwell(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
        this.inkwell.render(poseStack, buffer, light, overlay);
    }

    public void renderScroll(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, int color) {
        this.scrollTube.render(poseStack, buffer, light, overlay);
        poseStack.pushPose();
        poseStack.scale(1.2F, 1.2F, 1.2F);
        poseStack.translate(-0.08f, -0.08F, 0.09F);
        this.scrollRibbon.render(poseStack, buffer, light, overlay, color);
        poseStack.popPose();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        this.root.render(poseStack, vertexConsumer, i, i1, i2);
    }
}