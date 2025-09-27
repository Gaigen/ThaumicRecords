package team.torka.thaumicrecords;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import team.torka.thaumicrecords.registry.*;

@Mod(ThaumicRecords.MOD_ID)
public class ThaumicRecords {
    public static final String MOD_ID = "thaumicrecords";

    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumicRecords(IEventBus modEventBus) {
        BlockRegistry.REGISTER.register(modEventBus);
        BlockEntityRegistry.REGISTER.register(modEventBus);
        ItemRegistry.REGISTER.register(modEventBus);
        CreativeTabRegistry.REGISTER.register(modEventBus);
        AspectRegistry.ASPECTS.register(modEventBus);
    }

}
