package team.torka.thaumicrecords;

import com.mojang.logging.LogUtils;
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
    }

}
