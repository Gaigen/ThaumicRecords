package team.torka.thaumicrecords.registry;

import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.torka.thaumicrecords.ThaumicRecords;

public class FluidTypeRegistry {
    public static final DeferredRegister<FluidType> REGISTRAR = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> FLUX_GOO_TYPE = REGISTRAR.register("flux_goo", () -> new FluidType(FluidType.Properties.create()
            .density(2000)
            .viscosity(6000)
            .lightLevel(7)
            .canDrown(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canSwim(false)
            .supportsBoating(false)
            .motionScale(0.002D)
            .fallDistanceModifier(0.0F)));
}
