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

public class JarModel extends Model {
    private final ModelPart root;
    private ModelPart body;
    private ModelPart lid;
//    private ModelPart brine;

    public JarModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.body = root.getChild("Body");
        this.lid = root.getChild("Lid");
//        this.brine = root.getChild("Brine");
    }

    public ModelPart getBody() {
        return body;
    }

    public ModelPart getLid() {
        return lid;
    }

//    public ModelPart getBrine() {
//        return brine;
//    }

    // Если нужно рендерить всё сразу (например, для других случаев)
    public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, int color) {
        body.render(pose, consumer, light, overlay, color);
        lid.render(pose, consumer, light, overlay, color);
//        brine.render(pose, consumer, light, overlay, color);
    }


    public static LayerDefinition createLayerDefinition() {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Тело банки (Core) – как в оригинале: addBox(-5, -12, -5, 10, 12, 10) при точке (0,0,0)
        // В новой системе: Body занимает Y от -12 до 0 (низ на -12, верх на 0)
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Крышка + горлышко (Lid) – addBox(-3, 0, -3, 6, 2, 6) с точкой (0, -14, 0)
        // Занимает Y от -14 до -12 (находится над телом, т.к. -14 < -12, а Y идёт вверх)
        // Крышка будет сверху (Y=-12..-14)
        root.addOrReplaceChild("Lid", CubeListBuilder.create().texOffs(0, 24).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -14.0F, 0.0F)   // ключевая точка из оригинала
        );

//        // Жидкость (Brine) – опционально
//        root.addOrReplaceChild("Brine", CubeListBuilder.create().texOffs(0, 0)          // при необходимости измените смещение текстуры
//                .addBox(-4.0F, -11.0F, -4.0F, 8.0F, 11.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

}
