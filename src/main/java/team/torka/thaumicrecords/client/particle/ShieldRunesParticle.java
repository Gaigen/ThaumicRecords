package team.torka.thaumicrecords.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.torka.thaumicrecords.ThaumicRecords;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Port of TC4's FXShieldRunes — OBJ hemisphere with additive blending.
 * Renders OBJ in render() method with blend state switching.
 */
@OnlyIn(Dist.CLIENT)
public class ShieldRunesParticle extends TextureSheetParticle {

    private final int targetEntityId;
    private final float yaw;
    private final float pitch;
    private static final float FADE_MULTIPLIER = 3.0F;

    private static float[] objVertices;
    private static float[] objTexCoords;
    private static int[] objFaces;
    private static int[] objFaceTexIndices;
    private static boolean objLoaded = false;

    protected ShieldRunesParticle(ClientLevel level, double x, double y, double z, double entityId, double yaw, double pitch, SpriteSet sprites) {
        super(level, x, y, z);
        this.targetEntityId = (int) entityId;
        this.yaw = (float) yaw;
        this.pitch = (float) pitch;

        this.rCol = 0.0f;
        this.gCol = 0.0f;
        this.bCol = 0.0f;
        this.alpha = 0.0f;

        this.gravity = 0;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 8 + random.nextInt(4);
        this.hasPhysics = false;
        this.quadSize = 0.01f;

        if (!objLoaded) {
            loadObj();
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        Entity target = level.getEntity(targetEntityId);
        if (target != null) {
            this.x = target.getX();
            this.y = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0;
            this.z = target.getZ();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if (objVertices == null) {
            return;
        }

        float fade = (this.age + partialTick) / this.lifetime;
        int frame = Math.min(15, (int) (14.0f * fade) + 1);

        double px = this.xo + (this.x - this.xo) * partialTick;
        double py = this.yo + (this.y - this.yo) * partialTick;
        double pz = this.zo + (this.z - this.zo) * partialTick;
        double cx = px - camera.getPosition().x;
        double cy = py - camera.getPosition().y;
        double cz = pz - camera.getPosition().z;

        // Save current blend state
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        // Bind hemis frame texture and draw OBJ
        RenderSystem.setShaderTexture(0, ThaumicRecords.createRl("textures/models/hemis" + frame + ".png"));
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

        Matrix4f matrix = new Matrix4f();
        matrix.translate((float) cx, (float) cy, (float) cz);
        matrix.rotateY((float) Math.toRadians(180.0f - this.yaw));
        matrix.rotateX((float) Math.toRadians(-this.pitch));
        float scale = 0.4f * 1.8f;
        matrix.scale(scale, scale, scale);

        for (int i = 0; i < objFaces.length; i += 3) {
            addVertex(buf, matrix, objFaces[i], objFaceTexIndices[i]);
            addVertex(buf, matrix, objFaces[i + 1], objFaceTexIndices[i + 1]);
            addVertex(buf, matrix, objFaces[i + 2], objFaceTexIndices[i + 2]);
        }

        BufferUploader.drawWithShader(buf.build());

        // Restore state for the particle batch that draws after this
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_PARTICLES);
    }

    private void addVertex(BufferBuilder buf, Matrix4f matrix, int vertexIdx, int texIdx) {
        float x = objVertices[vertexIdx * 3];
        float y = objVertices[vertexIdx * 3 + 1];
        float z = objVertices[vertexIdx * 3 + 2];
        float u = objTexCoords[texIdx * 2];
        float v = objTexCoords[texIdx * 2 + 1];
        Vector3f pos = matrix.transformPosition(x, y, z, new Vector3f());
        buf.addVertex(pos.x, pos.y, pos.z).setUv(u, v);
    }

    private static void loadObj() {
        try {
            List<float[]> verts = new ArrayList<>();
            List<float[]> texs = new ArrayList<>();
            List<int[]> faces = new ArrayList<>();
            List<int[]> texFaces = new ArrayList<>();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(ShieldRunesParticle.class.getResourceAsStream("/assets/thaumicrecords/textures/models/hemis.obj")));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("v ")) {
                    String[] p = line.split("\\s+");
                    verts.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])});
                } else if (line.startsWith("vt ")) {
                    String[] p = line.split("\\s+");
                    texs.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2])});
                } else if (line.startsWith("f ")) {
                    String[] p = line.split("\\s+");
                    for (int i = 1; i <= 3; i++) {
                        String[] idx = p[i].split("/");
                        faces.add(new int[]{Integer.parseInt(idx[0]) - 1});
                        texFaces.add(new int[]{Integer.parseInt(idx[1]) - 1});
                    }
                }
            }
            reader.close();

            objVertices = new float[verts.size() * 3];
            for (int i = 0; i < verts.size(); i++) {
                objVertices[i * 3] = verts.get(i)[0];
                objVertices[i * 3 + 1] = verts.get(i)[1];
                objVertices[i * 3 + 2] = verts.get(i)[2];
            }
            objTexCoords = new float[texs.size() * 2];
            for (int i = 0; i < texs.size(); i++) {
                objTexCoords[i * 2] = texs.get(i)[0];
                objTexCoords[i * 2 + 1] = texs.get(i)[1];
            }
            objFaces = new int[faces.size()];
            for (int i = 0; i < faces.size(); i++) {
                objFaces[i] = faces.get(i)[0];
            }
            objFaceTexIndices = new int[texFaces.size()];
            for (int i = 0; i < texFaces.size(); i++) {
                objFaceTexIndices[i] = texFaces.get(i)[0];
            }
            objLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double entityId, double yaw, double pitch) {
            return new ShieldRunesParticle(level, x, y, z, entityId, yaw, pitch, this.sprites);
        }
    }
}
