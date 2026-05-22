package team.torka.thaumicrecords.node;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class NormalNode extends Node {

    public NormalNode() {
        super(ThaumicRecords.createTranslationKey("node_type", NodeType.NORMAL.getKey()), ThaumicRecords.createRl("textures/misc/node/" + NodeType.NORMAL.getKey() + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + NodeType.NORMAL.getKey() + ".png")),NodeType.NORMAL,200,1F,true);
    }
}
