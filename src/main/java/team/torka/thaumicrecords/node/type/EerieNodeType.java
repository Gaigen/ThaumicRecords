package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class EerieNodeType extends NodeType {
    private static final String KEY = "eerie";

    public EerieNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.translucentNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")));
    }
    // TODO 黑暗节点特殊逻辑
}
