package team.torka.thaumicrecords.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import team.torka.thaumicrecords.ThaumicRecords;

import javax.annotation.ParametersAreNonnullByDefault;

public class AuraNodeBreakParticle extends TextureSheetParticle {

    protected AuraNodeBreakParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 32;
        this.gravity = 0;
        this.hasPhysics = false;
        this.quadSize = 0.5f;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        super.render(buffer, camera, partialTicks);
    }

    @Override
    public float getU0() {
        int frame = Math.min(this.age, this.lifetime - 1);
        return (float) frame / 32.0F;
    }

    @Override
    public float getU1() {
        int frame = Math.min(this.age, this.lifetime - 1);
        return (float) (frame + 1) / 32.0F;
    }

    @Override
    public float getV0() {
        return 0.0F;
    }

    @Override
    public float getV1() {
        return 1.0F;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return NODE_RENDER_TYPE;
    }

    public static final ParticleRenderType NODE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ThaumicRecords.createRl("textures/misc/node/break.png"));
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "NODE_BREAK_ANIMATION";
        }
    };

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        @ParametersAreNonnullByDefault
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new AuraNodeBreakParticle(level, x, y, z);
        }
    }
}
