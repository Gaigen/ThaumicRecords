package team.torka.thaumicrecords.api.block;

import team.torka.thaumicrecords.api.aspect.AspectList;

public interface AspectRenderable {
    AspectList getAspectRendered();

    float getRenderYOffset();
}
