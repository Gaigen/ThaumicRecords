package team.torka.thaumicrecords;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import team.torka.thaumicrecords.registry.ArmorMaterialRegistry;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.CreativeTabRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.FeatureRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.MenuRegistry;
import team.torka.thaumicrecords.registry.NodeModifierRegistry;
import team.torka.thaumicrecords.registry.NodeTypeRegistry;
import team.torka.thaumicrecords.registry.ParticleRegistry;
import team.torka.thaumicrecords.registry.RecipeSerializerRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

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
        AttachmentRegistry.REGISTRAR.register(modEventBus);
        FeatureRegistry.REGISTRAR.register(modEventBus);

    }


    public static String createTranslationKey(String prefix, String path) {
        return prefix + "." + MOD_ID + "." + path;
    }

    public static ResourceLocation createRl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
