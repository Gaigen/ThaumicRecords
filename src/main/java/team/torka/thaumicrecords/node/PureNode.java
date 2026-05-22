package team.torka.thaumicrecords.node;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class PureNode extends Node {

    public PureNode() {
        super(ThaumicRecords.createTranslationKey("node_type", NodeType.PURE.getKey()), ThaumicRecords.createRl("textures/misc/node/" + NodeType.PURE.getKey() + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + NodeType.PURE.getKey() + ".png")),NodeType.PURE,250,0.75F,true);
    }
    // TODO 纯净节点
}
