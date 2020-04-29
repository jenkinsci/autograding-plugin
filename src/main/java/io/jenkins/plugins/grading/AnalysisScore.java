package io.jenkins.plugins.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;

/**
 * Computes the {@link AggregatedScore} impact of static analysis results. These results are obtained by inspecting a {@link
 * AnalysisResult} instance of the Warnings Next Generation plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class AnalysisScore extends Score {
    private static final long serialVersionUID = 1L;

    private final int errorsSize;
    private final int highPrioritySize;
    private final int normalPrioritySize;
    private final int lowPrioritySize;

    private final int totalSize;

    /**
     * Creates a new {@link AnalysisScore} instance.
     *
     * @param name
     *         the human readable name of the analysis tool
     * @param configuration
     *         the grading configuration
     * @param result
     *         the static analysis result
     */
    public AnalysisScore(final String name, final AnalysisConfiguration configuration, final AnalysisResult result) {
        super(result.getId(), name);

        this.errorsSize = result.getTotalErrorsSize();
        this.highPrioritySize = result.getTotalHighPrioritySize();
        this.normalPrioritySize = result.getTotalNormalPrioritySize();
        this.lowPrioritySize = result.getTotalLowPrioritySize();
        this.totalSize = result.getTotalSize();

        setTotalImpact(computeImpact(configuration));
    }

    private int computeImpact(final AnalysisConfiguration configuration) {
        int change = 0;

        change = change + configuration.getErrorImpact() * errorsSize;
        change = change + configuration.getHighImpact() * highPrioritySize;
        change = change + configuration.getNormalImpact() * normalPrioritySize;
        change = change + configuration.getLowImpact() * lowPrioritySize;

        return change;
    }

    public int getErrorsSize() {
        return errorsSize;
    }

    public int getHighPrioritySize() {
        return highPrioritySize;
    }

    public int getNormalPrioritySize() {
        return normalPrioritySize;
    }

    public int getLowPrioritySize() {
        return lowPrioritySize;
    }

    public int getTotalSize() {
        return totalSize;
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
                && highPrioritySize == that.highPrioritySize
                && normalPrioritySize == that.normalPrioritySize
                && lowPrioritySize == that.lowPrioritySize
                && totalSize == that.totalSize;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(errorsSize, highPrioritySize, normalPrioritySize, lowPrioritySize, totalSize);
    }
}
