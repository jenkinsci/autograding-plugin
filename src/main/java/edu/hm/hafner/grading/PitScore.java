package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;

/**
 * Computes the {@link AggregatedScore} impact of PIT mutation test results. These results are obtained by inspecting a
 * {@link PitBuildAction} instance of the PIT plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class PitScore extends Score {
    private static final long serialVersionUID = 1L;

    static final String ID = "pit";

    private final int mutationsSize;
    private final int undetectedSize;
    private final int undetectedPercentage;

    /**
     * Creates a new {@link PitScore} instance.
     *
     * @param displayName
     *         the human readable name of PIT
     * @param configuration
     *         the grading configuration
     * @param totalMutations
     *         total number of mutations
     * @param undetectedMutations
     *         number of undetected mutations
     */
    public PitScore(final String displayName, final PitConfiguration configuration, final int totalMutations,
            final int undetectedMutations) {
        super(ID, displayName);

        mutationsSize = totalMutations;
        undetectedSize = undetectedMutations;
        undetectedPercentage = undetectedSize * 100 / mutationsSize;

        setTotalImpact(computeImpact(configuration));
    }

    /**
     * Creates a new {@link PitScore} instance.
     *
     * @param configuration
     *         the grading configuration
     * @param totalMutations
     *         total number of mutations
     * @param undetectedMutations
     *         number of undetected mutations
     */
    public PitScore(final PitConfiguration configuration, final int totalMutations, final int undetectedMutations) {
        this("PIT Mutation Coverage", configuration, totalMutations, undetectedMutations);
    }

    private int computeImpact(final PitConfiguration configs) {
        int change = 0;

        change = change + configs.getUndetectedImpact() * getUndetectedSize();
        change = change + configs.getUndetectedPercentageImpact() * getUndetectedPercentage();
        change = change + configs.getDetectedImpact() * getDetectedSize();
        change = change + configs.getDetectedPercentageImpact() * getDetectedPercentage();

        return change;
    }

    public final int getMutationsSize() {
        return mutationsSize;
    }

    public final int getUndetectedSize() {
        return undetectedSize;
    }

    public final int getDetectedSize() {
        return mutationsSize - undetectedSize;
    }

    public final int getUndetectedPercentage() {
        return undetectedPercentage;
    }

    public final int getDetectedPercentage() {
        return 100 - undetectedPercentage;
    }

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PitScore pitScore = (PitScore) o;
        return mutationsSize == pitScore.mutationsSize
                && undetectedSize == pitScore.undetectedSize
                && undetectedPercentage == pitScore.undetectedPercentage;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(mutationsSize, undetectedSize, undetectedPercentage);
    }
}
