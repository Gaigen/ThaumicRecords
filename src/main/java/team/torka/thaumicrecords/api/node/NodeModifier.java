package team.torka.thaumicrecords.api.node;

public class NodeModifier {
    private final String translationKey;

    private final double regenFrequencyModifier;

    public NodeModifier(String translationKey, double regenFrequencyModifier) {
        this.translationKey = translationKey;
        this.regenFrequencyModifier = regenFrequencyModifier;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public double getRegenFrequencyModifier() {
        return regenFrequencyModifier;
    }
}
