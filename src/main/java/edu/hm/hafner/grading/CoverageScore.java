package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

/**
 * Computes the {@link AggregatedScore} impact of code coverage results. These results are obtained by inspecting a
 * {@link CoverageConfiguration} instance of the Code Coverage API plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class CoverageScore extends Score {
    private static final long serialVersionUID = 1L;

    private final int coveredSize;
    private final int missedSize;

    /**
     * Creates a new {@link CoverageScore} instance.
     *
     * @param id
     *         the ID of the coverage
     * @param displayName
     *         display name of the coverage type (like line or branch coverage)
     * @param configuration
     *         the grading configuration
     * @param coveredSize
     *         the percentage (covered)
     * @param missedSize
     *         the percentage (misses, i.e. not covered)
     */
    public CoverageScore(final String id, final String displayName, final CoverageConfiguration configuration,
            final int coveredSize, final int missedSize) {
        super(id, displayName);

        this.coveredSize = coveredSize;
        this.missedSize = missedSize;

        setTotalImpact(computeImpact(configuration));
    }

    private int computeImpact(final CoverageConfiguration configuration) {
        int change = 0;

        change = change + configuration.getMissedImpact() * missedSize;
        change = change + configuration.getCoveredImpact() * coveredSize;

        return change;
    }

    public int getCoveredSize() {
        return coveredSize;
    }

    public int getMissedSize() {
        return missedSize;
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoverageScore that = (CoverageScore) o;
        return coveredSize == that.coveredSize && missedSize == that.missedSize;
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(coveredSize, missedSize);
    }
}
