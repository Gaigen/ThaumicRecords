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

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneWorkbenchModel extends Model {
    private final ModelPart root;

    public ArcaneWorkbenchModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8.0F, 0.0F, -8.0F, 16.0F, 8.0F, 16.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        partdefinition.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-8.0F, 0.0F, -8.0F, 16.0F, 4.0F, 16.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));
        partdefinition.addOrReplaceChild("Leg1", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(3.0F, 16.0F, -7.0F));
        partdefinition.addOrReplaceChild("Leg2", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(-7.0F, 16.0F, 3.0F));
        partdefinition.addOrReplaceChild("Leg3", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(3.0F, 16.0F, 3.0F));
        partdefinition.addOrReplaceChild("Leg4", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(-7.0F, 16.0F, -7.0F));
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
