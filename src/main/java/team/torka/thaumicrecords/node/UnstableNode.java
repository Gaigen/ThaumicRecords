package team.torka.thaumicrecords.node;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class UnstableNode extends Node {

    public UnstableNode() {
        super(ThaumicRecords.createTranslationKey("node_type", NodeType.UNSTABLE.getKey()), ThaumicRecords.createRl("textures/misc/node/" + NodeType.UNSTABLE.getKey() + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + NodeType.UNSTABLE.getKey() + ".png")), NodeType.UNSTABLE, 100, 1, false);
    }
    // TODO 不稳定节点
}
