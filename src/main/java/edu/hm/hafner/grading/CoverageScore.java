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

    private final int coveredPercentage;

    /**
     * Creates a new {@link CoverageScore} instance.
     *
     * @param id
     *         the ID of the coverage
     * @param displayName
     *         display name of the coverage type (like line or branch coverage)
     * @param configuration
     *         the grading configuration
     * @param coveredPercentage
     *         the percentage (covered)
     */
    public CoverageScore(final String id, final String displayName, final CoverageConfiguration configuration,
            final int coveredPercentage) {
        super(id, displayName);

        this.coveredPercentage = coveredPercentage;

        setTotalImpact(computeImpact(configuration));
    }

    private int computeImpact(final CoverageConfiguration configuration) {
        int change = 0;

        change = change + configuration.getMissedPercentageImpact() * getMissedPercentage();
        change = change + configuration.getCoveredPercentageImpact() * coveredPercentage;

        return change;
    }

    public int getCoveredPercentage() {
        return coveredPercentage;
    }

    public int getMissedPercentage() {
        return 100 - coveredPercentage;
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
        return coveredPercentage == that.coveredPercentage;
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(coveredPercentage);
    }
}
