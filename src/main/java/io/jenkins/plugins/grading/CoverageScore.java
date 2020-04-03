package io.jenkins.plugins.grading;

import io.jenkins.plugins.coverage.targets.Ratio;

/**
 * Computes the {@link Score} impact of code coverage results. These results are obtained by inspecting a {@link
 * CoverageConfiguration} instance of the Code Coverage API plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class CoverageScore {
    private final String id;

    private final int totalImpact;

    private final int coveredSize;
    private final int missedSize;

    /**
     * Creates a new {@link CoverageScore} instance.
     *
     * @param configuration
     *         the grading configuration
     * @param ratio
     *         the coverage ratio
     */
    public CoverageScore(final CoverageConfiguration configuration, final Ratio ratio) {
        this.id = "Line";

        this.coveredSize = ratio.getPercentage();
        this.missedSize = 100 - ratio.getPercentage();

        totalImpact = computeImpact(configuration);
    }

    private int computeImpact(final CoverageConfiguration configuration) {
        int change = 0;

        change = change + configuration.getMissedImpact() * missedSize;
        change = change + configuration.getCoveredImpact() * coveredSize;

        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    public int getCoveredSize() {
        return coveredSize;
    }

    public int getMissedSize() {
        return missedSize;
    }
}
