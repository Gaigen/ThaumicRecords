package team.torka.thaumicrecords.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.world.biome.BiomeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public abstract class MultiNoiseBiomeSourceParameterListMixin {

    @Shadow
    private Climate.ParameterList<Holder<Biome>> parameters;

    /**
     * Detect if we're inside Bootstrap.validate() or VanillaRegistries.createLookup().
     * In that context, adding our biome would create an unreferenced holder because
     * NeoForge biome modifiers aren't built during vanilla validation.
     */
    private static boolean thaumicrecords$isVanillaBootstrapValidation() {
        return StackWalker.getInstance().walk(frames ->
                frames.anyMatch(frame -> {
                    String cls = frame.getClassName();
                    String method = frame.getMethodName();
                    return (cls.equals("net.minecraft.server.Bootstrap")
                            && method.equals("validate"))
                            || (cls.equals("net.minecraft.data.registries.VanillaRegistries")
                            && method.equals("createLookup"));
                })
        );
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void thaumicrecords$addMagicalForest(
            MultiNoiseBiomeSourceParameterList.Preset preset,
            HolderGetter<Biome> biomeGetter,
            CallbackInfo ci) {
        // Skip during vanilla bootstrap validation — would create unreferenced holder
        if (thaumicrecords$isVanillaBootstrapValidation()) {
            ThaumicRecords.LOGGER.debug("[Mixin] Skipping Magical Forest during vanilla registry validation");
            return;
        }

        // Skip during datagen
        if (Boolean.getBoolean("thaumicrecords.datagen")) {
            return;
        }

        if (preset != MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD) {
            return;
        }

        Holder.Reference<Biome> magicalForest;
        try {
            magicalForest = biomeGetter.getOrThrow(BiomeRegistry.MAGICAL_FOREST);
        } catch (Exception e) {
            // Biome not available in this lookup context — skip silently
            return;
        }

        // Forest-like climate niche — moderate temperature, humid, not extreme terrain
        Climate.ParameterPoint params = new Climate.ParameterPoint(
                Climate.Parameter.span(-0.15f, 0.20f),  // temperature — moderate, like forest
                Climate.Parameter.span(0.10f, 0.70f),   // humidity — humid, forest-like
                Climate.Parameter.span(-0.10f, 0.40f),  // continentalness — not deep ocean, not center
                Climate.Parameter.span(-0.20f, 0.30f),  // erosion — not mountains, not flat
                Climate.Parameter.point(0.0f),           // depth (surface)
                Climate.Parameter.span(-0.40f, 0.40f),  // weirdness — not extreme
                0L                                      // offset
        );

        List<Pair<Climate.ParameterPoint, Holder<Biome>>> values =
                new ArrayList<>(this.parameters.values());
        values.add(Pair.of(params, magicalForest));
        this.parameters = new Climate.ParameterList<>(values);

        ThaumicRecords.LOGGER.info("[Mixin] Magical forest added to overworld! Total: {}", values.size());
    }
}
