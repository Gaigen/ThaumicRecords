package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class PureNodeType extends NodeType {
    private static final String KEY = "pure";

    public PureNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")));
    }
    // TODO 纯净节点
}
