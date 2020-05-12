package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

/**
 * Computes the {@link AggregatedScore} impact of static analysis results. These results are obtained by summing up the
 * number of static analysis warnings.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class AnalysisScore extends Score {
    private static final long serialVersionUID = 1L;

    private final int errorsSize;
    private final int highSeveritySize;
    private final int normalSeveritySize;
    private final int lowSeveritySize;

    /**
     * Creates a new {@link AnalysisScore} instance.
     *
     * @param id
     *         the ID of the analysis tool
     * @param displayName
     *         the human readable name of the analysis tool
     * @param configuration
     *         the grading configuration
     * @param totalErrorsSize
     *         total number of errors
     * @param totalHighSeveritySize
     *         total number of warnings with severity high
     * @param totalNormalSeveritySize
     *         total number of warnings with severity normal
     * @param totalLowSeveritySize
     *         total number of warnings with severity low
     */
    public AnalysisScore(final String id, final String displayName, final AnalysisConfiguration configuration,
            final int totalErrorsSize,
            final int totalHighSeveritySize, final int totalNormalSeveritySize, final int totalLowSeveritySize) {
        super(id, displayName);

        this.errorsSize = totalErrorsSize;
        this.highSeveritySize = totalHighSeveritySize;
        this.normalSeveritySize = totalNormalSeveritySize;
        this.lowSeveritySize = totalLowSeveritySize;

        setTotalImpact(computeImpact(configuration));
    }

    private int computeImpact(final AnalysisConfiguration configuration) {
        int change = 0;

        change = change + configuration.getErrorImpact() * errorsSize;
        change = change + configuration.getHighImpact() * highSeveritySize;
        change = change + configuration.getNormalImpact() * normalSeveritySize;
        change = change + configuration.getLowImpact() * lowSeveritySize;

        return change;
    }

    public int getErrorsSize() {
        return errorsSize;
    }

    public int getHighSeveritySize() {
        return highSeveritySize;
    }

    public int getNormalSeveritySize() {
        return normalSeveritySize;
    }

    public int getLowSeveritySize() {
        return lowSeveritySize;
    }

    public int getTotalSize() {
        return getErrorsSize() + getHighSeveritySize() + getNormalSeveritySize() + getLowSeveritySize();
    }

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalysisScore that = (AnalysisScore) o;
        return errorsSize == that.errorsSize
                && highSeveritySize == that.highSeveritySize
                && normalSeveritySize == that.normalSeveritySize
                && lowSeveritySize == that.lowSeveritySize;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(errorsSize, highSeveritySize, normalSeveritySize, lowSeveritySize);
    }
}
