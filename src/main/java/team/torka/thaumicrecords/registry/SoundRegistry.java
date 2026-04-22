package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> REGISTRAR = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CRAFT_FAIL = REGISTRAR.register("craft_fail",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("craft_fail")));

    public static final DeferredHolder<SoundEvent, SoundEvent> NODE_BREAK = REGISTRAR.register("node_break",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("node_break")));
}