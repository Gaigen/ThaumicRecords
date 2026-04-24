package team.torka.thaumicrecords.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.data.component.WandItemComponent;

public class DataComponentRegistry {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,
            ThaumicRecords.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandItemComponent>> WAND_ITEM_DATA = REGISTRAR.register("wand_item_data",
            () -> DataComponentType.<WandItemComponent>builder()
                    .persistent(WandItemComponent.CODEC)
                    .networkSynchronized(WandItemComponent.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AspectListComponent>> ASPECT_LIST = REGISTRAR.register("aspect_list_data",
            () -> DataComponentType.<AspectListComponent>builder()
                    .persistent(AspectListComponent.CODEC)
                    .networkSynchronized(AspectListComponent.STREAM_CODEC)
                    .build());

}
