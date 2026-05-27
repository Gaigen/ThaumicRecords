package team.torka.thaumicrecords.client.event.listener;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import team.torka.thaumicrecords.item.ElementalShovelItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT)
public class ArchitectPreviewEventListener {

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof ElementalShovelItem shovel)) {
            return;
        }
        if (!player.isShiftKeyDown()) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        HitResult hit = player.pick(20.0, partialTick, false);
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos targetPos = blockHit.getBlockPos();
        Direction face = blockHit.getDirection();
        Level level = mc.level;

        List<BlockPos> architectBlocks = shovel.getArchitectBlocks(mainHand, level, targetPos, face, player);
        if (architectBlocks.isEmpty()) {
            return;
        }

        Set<BlockPos> blockSet = new HashSet<>(architectBlocks);
        int ticks = player.tickCount;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        // Additive blending + no depth test (matching original TC4 drawOverlayBlock)
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        var mat = poseStack.last();

        for (BlockPos pos : architectBlocks) {
            float r = Mth.sin(ticks / 2.0F + pos.getX()) * 0.2F + 0.3F;
            float g = Mth.sin(ticks / 3.0F + pos.getY()) * 0.2F + 0.3F;
            float b = Mth.sin(ticks / 4.0F + pos.getZ()) * 0.2F + 0.8F;
            float a = 0.2F;

            float x1 = pos.getX() - 0.001F;
            float y1 = pos.getY() - 0.001F;
            float z1 = pos.getZ() - 0.001F;
            float x2 = pos.getX() + 1.001F;
            float y2 = pos.getY() + 1.001F;
            float z2 = pos.getZ() + 1.001F;

            // Only render faces adjacent to air (connected texture behavior)
            // Bottom (Y-)
            if (!blockSet.contains(pos.below())) {
                addQuad(buf, mat, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
            }
            // Top (Y+)
            if (!blockSet.contains(pos.above())) {
                addQuad(buf, mat, r, g, b, a, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1);
            }
            // North (Z-)
            if (!blockSet.contains(pos.north())) {
                addQuad(buf, mat, r, g, b, a, x1, y2, z1, x2, y2, z1, x2, y1, z1, x1, y1, z1);
            }
            // South (Z+)
            if (!blockSet.contains(pos.south())) {
                addQuad(buf, mat, r, g, b, a, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
            }
            // West (X-)
            if (!blockSet.contains(pos.west())) {
                addQuad(buf, mat, r, g, b, a, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1);
            }
            // East (X+)
            if (!blockSet.contains(pos.east())) {
                addQuad(buf, mat, r, g, b, a, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
            }
        }

        MeshData mesh = buf.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }

    private static void addQuad(BufferBuilder buf, com.mojang.blaze3d.vertex.PoseStack.Pose mat, float r, float g, float b, float a, float x1, float y1,
                                float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        buf.addVertex(mat.pose(), x1, y1, z1).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x2, y2, z2).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x3, y3, z3).setColor(r, g, b, a);
        buf.addVertex(mat.pose(), x4, y4, z4).setColor(r, g, b, a);
    }
}
