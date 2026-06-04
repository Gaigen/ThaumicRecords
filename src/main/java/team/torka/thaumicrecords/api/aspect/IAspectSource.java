package team.torka.thaumicrecords.api.aspect;

/**
 * Marker interface for block entities that can supply essentia (e.g. jars).
 * Used by EssentiaHandler to discover sources during infusion crafting.
 */
public interface IAspectSource extends IEssentiaContainerEntity {
}
