package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class HungryNodeType extends NodeType {
    private static final String KEY = "hungry";

    public HungryNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")), 600, 0.75F, true);
    }
    // TODO 饕餮节点特殊逻辑
}
