package team.torka.thaumicrecords.api.aspect;

/**
 * Marker interface for block entities that can provide essentia to other blocks.
 * Used by EssentiaHandler to scan for nearby essentia sources (jars, alembics, etc.).
 * Blocks that implement this will be discovered during essentia scanning.
 */
public interface IAspectSource extends IEssentiaContainerEntity {
}
