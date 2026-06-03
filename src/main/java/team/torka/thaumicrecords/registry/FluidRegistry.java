package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.FiniteFluid;
import team.torka.thaumicrecords.block.FluxGooBlock;

public class FluidRegistry {
    public static final DeferredRegister<Fluid> REGISTRAR = DeferredRegister.create(BuiltInRegistries.FLUID, ThaumicRecords.MOD_ID);

    // Single fluid instance (not source + flowing)
    public static final DeferredHolder<Fluid, FiniteFluid> FLUX_GOO = REGISTRAR.register("flux_goo",
            () -> new FiniteFluid(FluidTypeRegistry.FLUX_GOO_TYPE::get, () -> ItemRegistry.FLUX_GOO_BUCKET.get(),
                    () -> (FluxGooBlock) BlockRegistry.FLUX_GOO.get()));
}
