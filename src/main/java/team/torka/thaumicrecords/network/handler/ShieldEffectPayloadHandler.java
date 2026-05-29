package team.torka.thaumicrecords.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.network.payload.ShieldEffectPayload;
import team.torka.thaumicrecords.registry.ParticleRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

/**
 * Port of TC4's PacketFXShield handler.
 * Calculates yaw/pitch from damage direction and spawns particles.
 */
public class ShieldEffectPayloadHandler {

    @OnlyIn(Dist.CLIENT)
    public static void handle(ShieldEffectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) {
                return;
            }

            Entity source = level.getEntity(payload.sourceEntityId());
            if (source == null) {
                return;
            }

            int target = payload.targetEntityId();

            // Play shield absorb sound locally on client
            level.playLocalSound(source.getX(), source.getY(), source.getZ(), SoundRegistry.RUNIC_SHIELD_EFFECT.get(), net.minecraft.sounds.SoundSource.PLAYERS,
                    0.66f, 1.0f, false  // no distance delay
            );

            if (target >= 0) {
                Entity damageSource = level.getEntity(target);
                if (damageSource != null) {
                    double d0 = source.getX() - damageSource.getX();
                    double d1 =
                            (source.getBoundingBox().minY + source.getBoundingBox().maxY) / 2.0 - (damageSource.getBoundingBox().minY + damageSource.getBoundingBox().maxY) / 2.0;
                    double d2 = source.getZ() - damageSource.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    float yaw = (float) (Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0f;
                    float pitch = (float) -(Math.atan2(d1, d3) * 180.0 / Math.PI);

                    spawnShieldParticle(level, source, yaw, pitch);
                } else {
                    spawnShieldParticle(level, source, 0.0f, 90.0f);
                }
            } else if (target == -1) {
                // Environmental (fire, drowning, etc.) — spawn in front of player
                spawnShieldParticle(level, source, source.getYRot(), 0.0f);
            } else if (target == -2) {
                spawnShieldParticle(level, source, 0.0f, 270.0f);
            } else if (target == -3) {
                spawnShieldParticle(level, source, 0.0f, 90.0f);
            }
        });
    }

    private static void spawnShieldParticle(ClientLevel level, Entity source, float yaw, float pitch) {
        // TC4 spawns at entity center: (posX, (minY+maxY)/2, posZ)
        // Pass entity ID via xd so particle can track the entity
        double x = source.getX();
        double y = (source.getBoundingBox().minY + source.getBoundingBox().maxY) / 2.0;
        double z = source.getZ();

        level.addParticle(ParticleRegistry.SHIELD_RUNES.get(), x, y, z, source.getId(),  // entity ID stored in xd
                yaw,             // yaw stored in yd
                pitch            // pitch stored in zd
        );
    }
}
