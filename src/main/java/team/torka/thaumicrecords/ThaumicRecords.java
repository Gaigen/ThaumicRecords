package team.torka.thaumicrecords;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import team.torka.thaumicrecords.registry.*;

@Mod(ThaumicRecords.MOD_ID)
public class ThaumicRecords {
    public static final String MOD_ID = "thaumicrecords";

    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumicRecords(IEventBus modEventBus) {
        BlockRegistry.REGISTRAR.register(modEventBus);
        BlockEntityRegistry.REGISTRAR.register(modEventBus);
        DataComponentRegistry.REGISTRAR.register(modEventBus);
        ItemRegistry.REGISTRAR.register(modEventBus);
        CreativeTabRegistry.REGISTRAR.register(modEventBus);
        AspectRegistry.REGISTRAR.register(modEventBus);
        WandCapRegistry.REGISTRAR.register(modEventBus);
        WandRodRegistry.REGISTRAR.register(modEventBus);
        NodeTypeRegistry.REGISTRAR.register(modEventBus);
        NodeModifierRegistry.REGISTRAR.register(modEventBus);
        SoundRegistry.REGISTRAR.register(modEventBus);
        ParticleRegistry.REGISTRAR.register(modEventBus);
        ArmorMaterialRegistry.REGISTRAR.register(modEventBus);
        RecipeTypeRegistry.REGISTRAR.register(modEventBus);
        RecipeSerializerRegistry.REGISTRAR.register(modEventBus);
        MenuRegistry.REGISTRAR.register(modEventBus);
    }

    public static String createTranslationKey(String prefix, String path) {
        return prefix + "." + MOD_ID + "." + path;
    }

    public static ResourceLocation createRl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
