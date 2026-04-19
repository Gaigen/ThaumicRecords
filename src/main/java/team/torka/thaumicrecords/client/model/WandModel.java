package team.torka.thaumicrecords.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WandModel {
    public final ModelPart rod;
    public final ModelPart cap;
    public final ModelPart capBottom;

    public WandModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("rod", CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-1.0F, -1.0F, -1.0F, 2, 18, 2),
                PartPose.offset(0.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("cap", CubeListBuilder.create().mirror()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("cap_bottom", CubeListBuilder.create().mirror()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
                PartPose.offset(0.0F, 20.0F, 0.0F));
        LayerDefinition layer = LayerDefinition.create(mesh, 32, 32);
        ModelPart bakedRoot = layer.bakeRoot();
        this.rod = bakedRoot.getChild("rod");
        this.cap = bakedRoot.getChild("cap");
        this.capBottom = bakedRoot.getChild("cap_bottom");
    }
}
