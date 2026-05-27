package team.torka.thaumicrecords.api.aspect;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface IEssentiaContainerEntity extends IEssentiaContainer {
    void setAspects(AspectList aspects);

    AspectList getAspects();

    // to add aspects to container (container must hold logic by itself, so bool is indicator of success)
    // true->aspects was consumed and added, false, aspects wasnt consumed and was not added
    boolean addAspect(ResourceLocation aspect, int amount);
    
    @Nullable
    Aspect getStoredAspect();

    @Nullable
    ResourceLocation getStoredAspectResource();

    int storedAmount();
}
