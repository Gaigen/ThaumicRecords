package team.torka.thaumicrecords.node.modifier;

import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeModifier;


public class NormalNodeModifier extends NodeModifier {
    private static final String KEY = "normal";

    public NormalNodeModifier() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY));
    }
}
