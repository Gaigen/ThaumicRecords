package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerEntity;
import team.torka.thaumicrecords.attachment.AspectListAttachment;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

public class JarBlockEntity extends BlockEntity implements IEssentiaContainerEntity {

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.JAR.get(), pos, state);
    }

    public void setAspects(AspectList aspects) {
        this.setData(AttachmentRegistry.ASPECT_LIST.get(), new AspectListAttachment(aspects));
        setChanged();                // помечаем блок для сохранения
    }

    public AspectList getAspects() {
        return this.getData(AttachmentRegistry.ASPECT_LIST.get()).aspects();
    }

    // Если нужно модифицировать существующий список (добавить аспект)
    public void addAspect(ResourceLocation aspect, int amount) {
        AspectList newList = getAspects().copy();   // копируем текущий список
        newList.add(aspect, amount);
        setAspects(newList);
    }


    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public int poursBy() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void onEmpty(ItemStack stack) {

    }
}
