package team.torka.thaumicrecords.node.type;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

public class NormalNodeType extends NodeType {
    private static final String KEY = "normal";

    public NormalNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"), 1.0f, 1.0f,
                CustomRenderType.additiveTransparency(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")));
    }
}
