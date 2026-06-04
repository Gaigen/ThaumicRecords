package team.torka.thaumicrecords.client.event.listener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import team.torka.thaumicrecords.client.particle.AuraNodeBreakParticle;
import team.torka.thaumicrecords.client.particle.CrucibleBubbleParticle;
import team.torka.thaumicrecords.client.particle.FluxGooBubbleParticle;
import team.torka.thaumicrecords.client.particle.SparkleParticle;
import team.torka.thaumicrecords.client.particle.RuneParticle;
import team.torka.thaumicrecords.client.particle.ShieldRunesParticle;
import team.torka.thaumicrecords.registry.ParticleRegistry;

@EventBusSubscriber(Dist.CLIENT)
public class RegisterParticleProvidersEventListener {

    @SubscribeEvent
    public static void onEvent(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.NODE_BREAK.get(), AuraNodeBreakParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.CRUCIBLE_BUBBLE.get(), CrucibleBubbleParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.SPARKLE.get(), SparkleParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.RUNE.get(), RuneParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.SHIELD_RUNES.get(), ShieldRunesParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.FLUX_GOO_BUBBLE.get(), FluxGooBubbleParticle.Provider::new);
    }
}
