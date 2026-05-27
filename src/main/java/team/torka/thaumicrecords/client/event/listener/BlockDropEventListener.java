package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.block.JarBlock;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;
import team.torka.thaumicrecords.registry.ItemRegistry;

@EventBusSubscriber(value = Dist.CLIENT)
public class BlockDropEventListener {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        // Проверяем, что блок - это наша банка
        if (event.getState().getBlock() instanceof JarBlock) {
            // Получаем блок-сущность из события
            BlockEntity blockEntity = event.getBlockEntity();
            if (blockEntity instanceof JarBlockEntity jar) {
                // Очищаем стандартные дропы (предметы-сущности)
                event.getDrops().clear();

                // Создаём предмет банки с аспектами
                ItemStack dropStack = new ItemStack(ItemRegistry.JAR.get());
                if (dropStack.getItem() instanceof IEssentiaContainerItem container) {
                    container.setAspects(dropStack, jar.getAspects());
                }

                // Создаём ItemEntity в позиции разрушенного блока
                ItemEntity itemEntity = new ItemEntity(event.getLevel(), event.getPos().getX() + 0.5D, event.getPos().getY() + 0.5D,
                        event.getPos().getZ() + 0.5D, dropStack);
                event.getDrops().add(itemEntity);
            }
        }
    }
}
