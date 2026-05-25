package team.torka.thaumicrecords.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 */
public class CrucibleBubbleParticle extends TextureSheetParticle {

    private static SpriteSet GLOBAL_SPRITES;

    private double bubbleSpeed;
    private int lifetime;
    private int age;

    protected CrucibleBubbleParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = 20;
        this.age = 0;
        this.bubbleSpeed = 0.002;
        // TC4: EntityFX.particleScale = rand*0.5+0.5 * (rand*0.3+0.2)
        // Rendered: 0.1 * scale
        this.quadSize = 0.1f * (random.nextFloat() * 0.5f + 0.5f) * (random.nextFloat() * 0.3f + 0.2f);
        this.alpha = 1.0f;
        setColor(1.0f, 0.0f, 0.5f);
        this.xd = (random.nextDouble() * 2.0 - 1.0) * 0.02;
        this.yd = random.nextDouble() * 0.02;
        this.zd = (random.nextDouble() * 2.0 - 1.0) * 0.02;
        pickSprite();
    }

    private void pickSprite() {
        if (GLOBAL_SPRITES != null) {
            pickSprite(GLOBAL_SPRITES);
        } else {
            setSprite(Minecraft.getInstance()
                    .getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
                    .apply(net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still")));
        }
    }

    public CrucibleBubbleParticle setFroth() {
        this.quadSize *= 0.75f;
        this.lifetime = 4 + random.nextInt(3);
        this.bubbleSpeed = -0.001;
        this.xd /= 5.0; this.yd /= 10.0; this.zd /= 5.0;
        return this;
    }

    public CrucibleBubbleParticle setFroth2() {
        this.quadSize *= 0.75f;
        this.lifetime = 12 + random.nextInt(12);
        this.bubbleSpeed = -0.005;
        this.xd /= 5.0; this.yd /= 10.0; this.zd /= 5.0;
        return this;
    }

    public CrucibleBubbleParticle setRGB(float r, float g, float b) {
        this.rCol = r; this.gCol = g; this.bCol = b;
        return this;
    }

    public CrucibleBubbleParticle setBubbleSpeed(double speed) {
        this.bubbleSpeed = speed;
        return this;
    }

    public static CrucibleBubbleParticle create(ClientLevel level, double x, double y, double z, int age) {
        CrucibleBubbleParticle p = new CrucibleBubbleParticle(level, x, y, z);
        if (age == 3) {
            p.lifetime = (age + 2) + (int)(8.0 / (p.random.nextDouble() * 0.8 + 0.2));
            p.bubbleSpeed = 0.002;
        } else if (age > 0) {
            p.lifetime = (age + 2) + (int)(8.0 / (p.random.nextDouble() * 0.8 + 0.2));
            p.bubbleSpeed = 0.002;
        }
        Minecraft.getInstance().particleEngine.add(p);
        return p;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void tick() {
        this.xo = x; this.yo = y; this.zo = z;
        if (this.age++ >= this.lifetime) { remove(); return; }
        this.yd += bubbleSpeed;
        if (bubbleSpeed > 0) {
            this.xd += (random.nextFloat() - random.nextFloat()) * 0.01;
            this.zd += (random.nextFloat() - random.nextFloat()) * 0.01;
        }
        move(xd, yd, zd);
        this.xd *= 0.85; this.yd *= 0.85; this.zd *= 0.85;
        if (lifetime - age <= 2) {
            this.alpha = Math.max(0, this.alpha - 0.3f);
        }
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) {
            GLOBAL_SPRITES = sprites;
        }

        @Override
        @ParametersAreNonnullByDefault
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double vx, double vy, double vz) {
            return new CrucibleBubbleParticle(level, x, y, z)
                    .setBubbleSpeed(vz > 0 ? 0.003 * vz : 0.002);
        }
    }
}