package team.torka.thaumicrecords.api.aspect;

import net.minecraft.resources.ResourceLocation;

public interface IEssentiaContainerEntity extends IEssentiaContainer {
    void setAspects(AspectList aspects);

    AspectList getAspects();

    // Если нужно модифицировать существующий список (добавить аспект)
    void addAspect(ResourceLocation aspect, int amount);
}
