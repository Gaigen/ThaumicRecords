package team.torka.thaumicrecords.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NODE_BREAK = REGISTRAR.register("node_break", () -> new SimpleParticleType(false));
}
