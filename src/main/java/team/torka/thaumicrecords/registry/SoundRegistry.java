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
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("craft_fail")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BUTTON_CLICK = REGISTRAR.register("button_click",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("button_click")));

    public static final DeferredHolder<SoundEvent, SoundEvent> HHON = REGISTRAR.register("hhon",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("hhon")));

    public static final DeferredHolder<SoundEvent, SoundEvent> HHOFF = REGISTRAR.register("hhoff",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("hhoff")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRITE = REGISTRAR.register("write",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("write")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ERASE = REGISTRAR.register("erase",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("erase")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CAMERA_TICKS = REGISTRAR.register("camera_ticks",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("camera_ticks")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPILL = REGISTRAR.register("spill",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("spill")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLE = REGISTRAR.register("bubble",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("bubble")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CRAFT_START = REGISTRAR.register("craft_start",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("craft_start")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WAND = REGISTRAR.register("wand",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("wand")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WAND_FAIL = REGISTRAR.register("wand_fail",
            () -> SoundEvent.createVariableRangeEvent(ThaumicRecords.createRl("wand_fail")));

}