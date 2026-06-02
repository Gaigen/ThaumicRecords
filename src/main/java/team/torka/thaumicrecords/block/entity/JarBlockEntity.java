package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IAspectSource;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerEntity;
import team.torka.thaumicrecords.api.block.AspectRenderable;
import team.torka.thaumicrecords.attachment.AspectListAttachment;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.Nullable;

public class JarBlockEntity extends BlockEntity implements IAspectSource, AspectRenderable {

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.JAR.get(), pos, state);
    }

    @Override
    public int storedAmount() {
        int myAmount = 0;
        AspectList list = getAspects();   // копируем текущий список
        if (!list.isEmpty()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(list.firstEntry().getKey());

            myAmount = list.get(AspectRegistry.ASPECT_REGISTRY.getKey(aspect));
        }
        return myAmount;
    }

    @Override
    @Nullable
    public ResourceLocation getStoredAspectResource() {
        AspectList list = getAspects();   // копируем текущий список
        if (!list.isEmpty()) {
            return list.firstEntry().getKey();
        }
        return null;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.setData(AttachmentRegistry.ASPECT_LIST.get(), new AspectListAttachment(aspects));
        setChanged(); // помечаем блок для сохранения
    }

    @Override
    public AspectList getAspects() {
        return this.getData(AttachmentRegistry.ASPECT_LIST.get()).aspects();
    }

    // Если нужно модифицировать существующий список (добавить аспект)
    @Override
    public boolean addAspect(ResourceLocation aspect, int amount) {
        if (!isLiquid()) {
            return false;
        }

        ResourceLocation storedAspect = getStoredAspectResource();

        if (storedAspect != null && !aspect.equals(storedAspect)) {
            return false;
        }

        int storedAmount = storedAmount();
        if (storedAmount + amount > capacity()) {
            return false;
        }
        AspectList list = getAspects().copy();   // копируем текущий список
        list.add(aspect, amount);
        setAspects(list);
        return true;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isLiquid() {
        return true;
    }

    @Override
    public int poursBy() {
        return 64;
    }

    @Override
    public int capacity() {
        return 64;
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void wasPoured(ResourceLocation aspect, int amount) {
        ResourceLocation storedAspect = getStoredAspectResource();
        int storedAmount = storedAmount();
        AspectList list = getAspects().copy();   // копируем текущий список
        list.take(aspect, amount);
        setAspects(list);
        if (amount >= storedAmount) {
            onEmpty();
        }
    }

    @Override
    public boolean canBePartiallyPoured() {
        return true;
    }

    @Override
    public boolean canHoldMultipleAspects() {
        return false;
    }

    @Override
    public Aspect getStoredAspect() {
        AspectList list = getAspects();   // копируем текущий список
        if (!list.isEmpty()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(list.firstEntry().getKey());
            return aspect;
        }
        return null;
    }

    @Override
    public AspectList getAspectRendered() {
        return getAspects().copy();
    }

    @Override
    public float getRenderYOffset() {
        return 0;
    }
}
