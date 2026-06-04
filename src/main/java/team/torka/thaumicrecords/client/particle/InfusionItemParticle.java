package team.torka.thaumicrecords.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Infusion item texture particle — ported from TC4's FXBoreParticles.
 * Renders the actual item/block texture as a particle flying from pedestal to matrix.
 * Used for ingredient absorption animation (67% of ticks, with 33% purple sparkle).
 */
public class InfusionItemParticle extends SingleQuadParticle {

    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float u0, u1, v0, v1;
    private final int itemColor;

    public InfusionItemParticle(ClientLevel level, double x, double y, double z, double tx, double ty, double tz, ItemStack stack, int face) {
        super(level, x, y, z);

        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;

        // Get item's texture sprite from the model
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getItemRenderer().getModel(stack, level, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon();

        // UV from the sprite
        this.u0 = sprite.getU0();
        this.u1 = sprite.getU1();
        this.v0 = sprite.getV0();
        this.v1 = sprite.getV1();

        // TC4: particleScale = rand*0.3 + 0.4
        this.quadSize = random.nextFloat() * 0.3F + 0.4F;

        // Item color tint (TC4: func_70596_a applies color multiplier)
        this.itemColor = 0xFFFFFF; // default white, could be tinted
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;

        // Lifetime based on distance (TC4: dist * 3)
        double dx = tx - this.x;
        double dy = ty - this.y;
        double dz = tz - this.z;
        int base = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + random.nextInt(base);

        // Initial random motion (TC4 drawInfusionParticles1: gaussian * 0.03 for items)
        float f3 = 0.03F;
        this.xd = random.nextGaussian() * f3;
        this.yd = random.nextGaussian() * f3;
        this.zd = random.nextGaussian() * f3;

        // Physics
        this.gravity = 0.2F;
        this.hasPhysics = true;
        this.alpha = 0.3F; // TC4: setAlphaF(0.3F)
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

        if (this.hasPhysics) {
            pushOutOfBlocks(this.x, this.y, this.z);
        }

        this.move(this.xd, this.yd, this.zd);

        // Damping
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;

        // Attraction (TC4: force 0.3, doubles to 0.6 when < 4 blocks)
        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double force = 0.3D;
        if (dist < 4.0D) {
            this.quadSize *= 0.9F;
            force = 0.6D;
        }

        if (this.quadSize < 0.05F) {
            this.remove();
            return;
        }

        if (Mth.floor(this.x) == Mth.floor(this.targetX) && Mth.floor(this.y) == Mth.floor(this.targetY) && Mth.floor(this.z) == Mth.floor(this.targetZ)) {
            this.remove();
            return;
        }

        if (dist > 0.01D) {
            dx /= dist;
            dy /= dist;
            dz /= dist;
            this.xd += dx * force;
            this.yd += dy * force;
            this.zd += dz * force;
        }

        this.xd = Mth.clamp(this.xd, -0.35F, 0.35F);
        this.yd = Mth.clamp(this.yd, -0.35F, 0.35F);
        this.zd = Mth.clamp(this.zd, -0.35F, 0.35F);
    }

    @Override
    public float getQuadSize(float partialTick) {
        // TC4: 0.1F * particleScale * (no bob for items)
        return 0.1F * this.quadSize;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240 << 16 | 240;
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

    // UV from item's sprite
    @Override
    protected float getU0() {
        return u0;
    }

    @Override
    protected float getU1() {
        return u1;
    }

    @Override
    protected float getV0() {
        return v0;
    }

    @Override
    protected float getV1() {
        return v1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return INFUSION_ITEM_RENDER_TYPE;
    }

    /**
     * Custom render type for item texture particles.
     * Uses the block atlas (same as item textures) with alpha blending.
     */
    public static final ParticleRenderType INFUSION_ITEM_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            // Bind the block atlas (where item textures live)
            RenderSystem.setShaderTexture(0, net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "THAUMIC_INFUSION_ITEM";
        }
    };
}
