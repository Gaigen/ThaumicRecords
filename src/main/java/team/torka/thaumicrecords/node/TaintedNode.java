package team.torka.thaumicrecords.node;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class TaintedNode extends Node {

    public TaintedNode() {
        super(ThaumicRecords.createTranslationKey("node_type", NodeType.TAINTED.getKey()), ThaumicRecords.createRl("textures/misc/node/" + NodeType.TAINTED.getKey() + ".png"),
                CustomRenderType.translucentNoDepth(ThaumicRecords.createRl("textures/misc/node/" + NodeType.TAINTED.getKey() + ".png")),NodeType.TAINTED,150,1F,true);
    }
    // TODO 污染节点
}
