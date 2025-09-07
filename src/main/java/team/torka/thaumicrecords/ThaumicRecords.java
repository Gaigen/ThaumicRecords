package team.torka.thaumicrecords;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import team.torka.thaumicrecords.register.TRBlockEntities;
import team.torka.thaumicrecords.register.TRBlocks;
import team.torka.thaumicrecords.register.TRCreativeTabs;
import team.torka.thaumicrecords.register.TRItems;

@Mod(ThaumicRecords.MOD_ID)
public class ThaumicRecords {
    public static final String MOD_ID = "thaumicrecords";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumicRecords(IEventBus modEventBus, ModContainer modContainer) {
        TRBlocks.REGISTER.register(modEventBus);
        TRBlockEntities.REGISTER.register(modEventBus);
        TRItems.REGISTER.register(modEventBus);
        TRCreativeTabs.REGISTER.register(modEventBus);
    }

}
