package team.torka.thaumicrecords.api.node;

public class NodeModifier {
    private final String translationKey;

    public NodeModifier(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
