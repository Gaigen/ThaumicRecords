package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class TaintedNodeType extends NodeType {
    private static final String KEY = "tainted";

    public TaintedNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.translucentNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")));
    }
    // TODO 污染节点
}
