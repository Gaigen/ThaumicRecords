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

public class FluxGooBubbleParticle extends TextureSheetParticle {

    private static SpriteSet GLOBAL_SPRITES;

    private double bubbleSpeed;
    private int lifetime;
    private int age;

    protected FluxGooBubbleParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = 20;
        this.age = 0;
        this.bubbleSpeed = 0.002D;
        this.quadSize = 0.1f * (random.nextFloat() * 0.5f + 0.5f) * (random.nextFloat() * 0.3f + 0.2f);
        this.alpha = 1.0f;
        setColor(1.0f, 0.0f, 0.5f);
        this.xd = (random.nextDouble() * 2.0D - 1.0D) * 0.02D;
        this.yd = random.nextDouble() * 0.02D;
        this.zd = (random.nextDouble() * 2.0D - 1.0D) * 0.02D;
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

    @Override
    @ParametersAreNonnullByDefault
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        if (this.age++ >= this.lifetime) {
            remove();
            return;
        }
        this.yd += bubbleSpeed;
        if (bubbleSpeed > 0) {
            this.xd += (random.nextFloat() - random.nextFloat()) * 0.01D;
            this.zd += (random.nextFloat() - random.nextFloat()) * 0.01D;
        }
        move(xd, yd, zd);
        this.xd *= 0.85D;
        this.yd *= 0.85D;
        this.zd *= 0.85D;
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new FluxGooBubbleParticle(level, x, y, z);
        }
    }
}
