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
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class JarModel extends Model {

    private final ModelPart root;

    public JarModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
    }

    public static LayerDefinition createLayerDefinition() {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Тело банки (Core) – как в оригинале: addBox(-5, -12, -5, 10, 12, 10) при точке (0,0,0)
        // В новой системе: Body занимает Y от -12 до 0 (низ на -12, верх на 0)
        root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // Крышка + горлышко (Lid) – addBox(-3, 0, -3, 6, 2, 6) с точкой (0, -14, 0)
        // Занимает Y от -14 до -12 (находится над телом, т.к. -14 < -12, а Y идёт вверх)
        // Крышка будет сверху (Y=-12..-14)
        root.addOrReplaceChild(
                "Lid",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -14.0F, 0.0F)   // ключевая точка из оригинала
        );

//        // Жидкость (Brine) – опционально
//        root.addOrReplaceChild(
//                "Brine",
//                CubeListBuilder.create()
//                        .texOffs(0, 0)
//                        .addBox(-4.0F, -11.0F, -4.0F, 8.0F, 10.0F, 8.0F),
//                PartPose.offset(0.0F, 0.0F, 0.0F)
//        );

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        root.render(
                poseStack,
                buffer,
                packedLight,
                packedOverlay,
                color
        );
    }
}
