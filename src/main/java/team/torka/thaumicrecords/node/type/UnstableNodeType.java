package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class UnstableNodeType extends NodeType {
    private static final String KEY = "unstable";

    public UnstableNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")), 100, 1, false);
    }
    // TODO 不稳定节点
}
