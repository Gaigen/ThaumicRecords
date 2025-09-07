package team.torka.thaumicrecords.api.aspect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AspectContainer extends ConcurrentHashMap<Aspect, Integer> {

    public static AspectContainer byMap(Map<Aspect, Integer> aspectMap) {
        AspectContainer aspectContainer =new AspectContainer();
        aspectContainer.putAll(aspectMap);
        return aspectContainer;
    }
}
