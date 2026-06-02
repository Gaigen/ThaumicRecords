package team.torka.thaumicrecords.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import team.torka.thaumicrecords.ThaumicRecords;

/**
 * Custom essentia trail particle based on TC4's FXEssentiaTrail.
 * Uses a custom ParticleRenderType that binds the thaumic particles texture directly,
 * with additive blending for a glowing effect.
 * <p>
 * TC4 original: FXEssentiaTrail extends EntityFX, uses particle sprite index 24 (white dot),
 * renders with alpha=0.5 and brightness=240, physics-based attraction to target.
 */
public class EssentiaTrailParticle extends SingleQuadParticle {

    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final int startCount;

    public EssentiaTrailParticle(ClientLevel level, double x, double y, double z, double tx, double ty, double tz, int count, float r, float g, float b,
                                 float scale) {
        super(level, x, y, z);

        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.startCount = count;

        // Color with proportional variation per channel (matches TC4 exactly)
        float mr = r * 0.2F;
        float mg = g * 0.2F;
        float mb = b * 0.2F;
        this.rCol = r - mr + random.nextFloat() * mr;
        this.gCol = g - mg + random.nextFloat() * mg;
        this.bCol = b - mb + random.nextFloat() * mb;

        // Scale — matches TC4: sin(count/2) * 0.1 + 1.0, then * scale
        this.quadSize = (Mth.sin(count / 2.0F) * 0.1F + 1.0F) * scale;

        // Lifetime: based on distance to target (matches TC4 exactly)
        double dx = tx - this.x;
        double dy = ty - this.y;
        double dz = tz - this.z;
        int base = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 30.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + random.nextInt(base);

        // Initial motion: sin-wave pattern (matches TC4 exactly)
        this.xd = Mth.sin(count / 4.0F) * 0.015F + random.nextGaussian() * 0.002;
        this.yd = 0.1F + Mth.sin(count / 3.0F) * 0.01F;
        this.zd = Mth.sin(count / 2.0F) * 0.015F + random.nextGaussian() * 0.002;

        // Physics (matches TC4: particleGravity = 0.2, noClip = false)
        this.gravity = 0.2F;
        this.hasPhysics = true;
        this.alpha = 0.5F;
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

        // Upward buoyancy — matches TC4: motionY += 0.01 * particleGravity
        this.yd += 0.01D * this.gravity;

        // Push out of blocks (matches TC4)
        if (this.hasPhysics) {
            pushOutOfBlocks(this.x, this.y, this.z);
        }

        // Move
        this.move(this.xd, this.yd, this.zd);

        // Damping (matches TC4 exactly)
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;

        // Speed cap (matches TC4 exactly)
        this.xd = Mth.clamp(this.xd, -0.05F, 0.05F);
        this.yd = Mth.clamp(this.yd, -0.05F, 0.05F);
        this.zd = Mth.clamp(this.zd, -0.05F, 0.05F);

        // Attraction to target (matches TC4: d13 = 0.01)
        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Shrink when close to target (matches TC4)
        if (dist < 2.0D) {
            this.quadSize *= 0.98F;
        }

        if (this.quadSize < 0.2F) {
            this.remove();
            return;
        }

        // Normalize direction and apply attraction force (matches TC4 exactly)
        if (dist > 0.01D) {
            dx /= dist;
            dy /= dist;
            dz /= dist;
            double force = 0.01D / Math.min(1.0D, dist);
            this.xd += dx * force;
            this.yd += dy * force;
            this.zd += dz * force;
        }
    }

    @Override
    public float getQuadSize(float partialTick) {
        // Scale pulsation — matches TC4: sin((particleAge - count) / 5.0) * 0.25 + 1.0
        float s = Mth.sin((this.age + partialTick - this.startCount) / 5.0F) * 0.25F + 1.0F;
        return 0.1F * this.quadSize * s;
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Full brightness like TC4 (tessellator.setBrightness(240))
        return 240 << 16 | 240;
    }

    private void pushOutOfBlocks(double x, double y, double z) {
        int bx = Mth.floor(x);
        int by = Mth.floor(y);
        int bz = Mth.floor(z);
        BlockPos pos = new BlockPos(bx, by, bz);

        // Only push out of solid full-cube blocks (matches TC4: isBlockNormalCubeDefault)
        if (!this.level.getBlockState(pos).isAir() && this.level.getBlockState(pos).isSolid()) {
            double ox = x - bx;
            double oy = y - by;
            double oz = z - bz;

            boolean left = !this.level.getBlockState(new BlockPos(bx - 1, by, bz)).isAir() && this.level.getBlockState(new BlockPos(bx - 1, by, bz)).isSolid();
            boolean right = !this.level.getBlockState(new BlockPos(bx + 1, by, bz)).isAir() && this.level.getBlockState(new BlockPos(bx + 1, by, bz)).isSolid();
            boolean down = !this.level.getBlockState(new BlockPos(bx, by - 1, bz)).isAir() && this.level.getBlockState(new BlockPos(bx, by - 1, bz)).isSolid();
            boolean up = !this.level.getBlockState(new BlockPos(bx, by + 1, bz)).isAir() && this.level.getBlockState(new BlockPos(bx, by + 1, bz)).isSolid();
            boolean back = !this.level.getBlockState(new BlockPos(bx, by, bz - 1)).isAir() && this.level.getBlockState(new BlockPos(bx, by, bz - 1)).isSolid();
            boolean front = !this.level.getBlockState(new BlockPos(bx, by, bz + 1)).isAir() && this.level.getBlockState(new BlockPos(bx, by, bz + 1)).isSolid();

            byte bestDir = -1;
            double bestDist = 9999.0D;

            if (left && ox < bestDist) {
                bestDist = ox;
                bestDir = 0;
            }
            if (right && 1.0D - ox < bestDist) {
                bestDist = 1.0D - ox;
                bestDir = 1;
            }
            if (down && oy < bestDist) {
                bestDist = oy;
                bestDir = 2;
            }
            if (up && 1.0D - oy < bestDist) {
                bestDist = 1.0D - oy;
                bestDir = 3;
            }
            if (back && oz < bestDist) {
                bestDist = oz;
                bestDir = 4;
            }
            if (front && 1.0D - oz < bestDist) {
                bestDist = 1.0D - oz;
                bestDir = 5;
            }

            float push = random.nextFloat() * 0.05F + 0.025F;
            float drift = (random.nextFloat() - random.nextFloat()) * 0.1F;

            switch (bestDir) {
                case 0 -> {
                    this.xd = -push;
                    this.yd = this.zd = drift;
                }
                case 1 -> {
                    this.xd = push;
                    this.yd = this.zd = drift;
                }
                case 2 -> {
                    this.yd = -push;
                    this.xd = this.zd = drift;
                }
                case 3 -> {
                    this.yd = push;
                    this.xd = this.zd = drift;
                }
                case 4 -> {
                    this.zd = -push;
                    this.yd = this.xd = drift;
                }
                case 5 -> {
                    this.zd = push;
                    this.yd = this.xd = drift;
                }
            }
        }
    }

    // UV mapping: TC4 uses sprite 24 on a 16x16 grid (0.5625-0.625 U, 0.0625-0.125 V)
    // We use the same UV range from our custom particle texture
    @Override
    protected float getU0() {
        return 0.5625F;
    }

    @Override
    protected float getU1() {
        return 0.625F;
    }

    @Override
    protected float getV0() {
        return 0.0625F;
    }

    @Override
    protected float getV1() {
        return 0.125F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ESSENTIA_TRAIL_RENDER_TYPE;
    }

    /**
     * Custom render type that binds the thaumic particles texture with additive blending.
     * Matches TC4's rendering: GL11.glColor4f(1,1,1,0.5), brightness=240, alpha blending.
     */
    public static final ParticleRenderType ESSENTIA_TRAIL_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ThaumicRecords.createRl("textures/misc/particles.png"));
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMIC_ESSENTIA_TRAIL";
        }
    };
}
