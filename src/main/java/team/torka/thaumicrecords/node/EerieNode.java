package team.torka.thaumicrecords.node;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class EerieNode extends Node {

    public EerieNode() {
        super(ThaumicRecords.createTranslationKey("node_type", NodeType.EERIE.getKey()), ThaumicRecords.createRl("textures/misc/node/" + NodeType.EERIE.getKey() + ".png"),
                CustomRenderType.translucentNoDepth(ThaumicRecords.createRl("textures/misc/node/" + NodeType.EERIE.getKey() + ".png")),NodeType.EERIE,100,0.5F,true);
    }
    // TODO 黑暗节点特殊逻辑
}
