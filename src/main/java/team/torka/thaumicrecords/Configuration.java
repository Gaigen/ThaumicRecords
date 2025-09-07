package team.torka.thaumicrecords;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configuration {
    public static final Configuration CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    private Configuration(ModConfigSpec.Builder builder) {
        // Define properties used by the configuration
        // ...
    }

    //CONFIG and CONFIG_SPEC are both built from the same builder, so we use a static block to seperate the properties
    static {
        Pair<Configuration, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Configuration::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
