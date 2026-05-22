package team.torka.thaumicrecords.api.node;

import java.util.Arrays;
import java.util.Optional;

public enum NodeType {
    NORMAL("normal", 0),
    EERIE("eerie", 1),
    PURE("pure", 2),
    HUNGRY("hungry", 3),
    TAINTED("tainted", 4),
    UNSTABLE("unstable", 5);

    private final String key;
    private final int id;

    NodeType(String key, int id){
        this.key = key;
        this.id = id;
    }

    public String getKey(){
        return this.key;
    }

    public static NodeType getById(int id){
        Optional<NodeType> nodeType = Arrays.stream(NodeType.values()).filter(type -> type.id == id).findFirst();
        if(nodeType.isEmpty()) return NodeType.NORMAL;
        return nodeType.get();
    }
}
