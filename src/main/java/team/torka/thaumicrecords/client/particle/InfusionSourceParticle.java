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

/**
 * Infusion source sparkle particle — exact port of TC4's FXBoreSparkle.
 * Used for ingredient absorption animation (33% of ticks).
 * <p>
 * TC4 original:
 * - particle = 24 (base sprite index)
 * - Animates through 4 frames: particleAge % 4
 * - UV: U = (particleAge%4)/16, V = 0.25 (row 4 of 16x16 grid)
 * - Color: set by caller (drawInfusionParticles3 sets purple)
 * - Scale: rand*0.5 + 0.5
 * - Gravity: 0.2
 * - Attraction: force 0.3, doubles to 0.6 when < 4 blocks
 * - Speed cap: 0.35
 * - Bob: sin(age/3) * 0.5 + 1.0
 */
public class InfusionSourceParticle extends SingleQuadParticle {

    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public InfusionSourceParticle(ClientLevel level, double x, double y, double z, double tx, double ty, double tz, float r, float g, float b) {
        super(level, x, y, z);

        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;

        // Color (set by caller — purple for drawInfusionParticles3)
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;

        // TC4: particleScale = rand*0.5 + 0.5
        this.quadSize = random.nextFloat() * 0.5F + 0.5F;

        // Lifetime based on distance (TC4: dist * 3)
        double dx = tx - this.x;
        double dy = ty - this.y;
        double dz = tz - this.z;
        int base = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + random.nextInt(base);

        // TC4: initial motion gaussian * 0.01
        float f3 = 0.01F;
        this.xd = random.nextGaussian() * f3;
        this.yd = random.nextGaussian() * f3;
        this.zd = random.nextGaussian() * f3;

        // TC4: gravity=0.2, noClip=false
        this.gravity = 0.2F;
        this.hasPhysics = true;
        this.alpha = 0.75F; // TC4: GL11.glColor4f(1,1,1,0.75)
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

        // TC4: pushOutOfBlocks
        if (this.hasPhysics) {
            pushOutOfBlocks(this.x, this.y, this.z);
        }

        // TC4: moveEntity
        this.move(this.xd, this.yd, this.zd);

        // TC4: damping 0.985
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;

        // TC4: attraction to target
        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // TC4: force = 0.3, doubles to 0.6 when < 4 blocks
        double force = 0.3D;
        if (dist < 4.0D) {
            this.quadSize *= 0.9F;
            force = 0.6D;
        }

        if (this.quadSize < 0.05F) {
            this.remove();
            return;
        }

        // TC4: die when reached target
        if (Mth.floor(this.x) == Mth.floor(this.targetX) && Mth.floor(this.y) == Mth.floor(this.targetY) && Mth.floor(this.z) == Mth.floor(this.targetZ)) {
            this.remove();
            return;
        }

        // TC4: normalize and apply force
        if (dist > 0.01D) {
            dx /= dist;
            dy /= dist;
            dz /= dist;
            this.xd += dx * force;
            this.yd += dy * force;
            this.zd += dz * force;
        }

        // TC4: speed cap 0.35
        this.xd = Mth.clamp(this.xd, -0.35F, 0.35F);
        this.yd = Mth.clamp(this.yd, -0.35F, 0.35F);
        this.zd = Mth.clamp(this.zd, -0.35F, 0.35F);
    }

    @Override
    public float getQuadSize(float partialTick) {
        // TC4: 0.1F * particleScale * sin(age/3)*0.5+1.0
        float bob = Mth.sin((this.age + partialTick) / 3.0F) * 0.5F + 1.0F;
        return 0.1F * this.quadSize * bob;
    }

    @Override
    protected int getLightColor(float partialTick) {
        // TC4: brightness 240
        return 240 << 16 | 240;
    }

    // TC4: animates through 4 sprites in row 4 (V=0.25)
    @Override
    protected float getU0() {
        return (this.age % 4) / 16.0F;
    }

    @Override
    protected float getU1() {
        return (this.age % 4) / 16.0F + 0.0624375F;
    }

    @Override
    protected float getV0() {
        return 0.25F;
    }

    @Override
    protected float getV1() {
        return 0.25F + 0.0624375F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return SPARKLE_RENDER_TYPE;
    }

    private void pushOutOfBlocks(double x, double y, double z) {
        int bx = Mth.floor(x);
        int by = Mth.floor(y);
        int bz = Mth.floor(z);
        BlockPos pos = new BlockPos(bx, by, bz);

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

    /**
     * Custom render type that binds the particle atlas (same texture as standard particles).
     * TC4 uses sprite 24 from the standard particle sprite sheet.
     */
    public static final ParticleRenderType SPARKLE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            // Bind the standard particle atlas (same as TC4's particle sprite sheet)
            RenderSystem.setShaderTexture(0, net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMIC_SPARKLE";
        }
    };
}
