package io.jenkins.plugins.grading;

import edu.hm.hafner.util.Ensure;
import io.jenkins.plugins.analysis.core.model.AnalysisResult;

/**
 * Computes the {@link Score} impact of static analysis results. These results are obtained by inspecting a
 * {@link AnalysisResult} instance of the Warnings Next Generation plugin.
 *
 * @author Eva-Maria Zeintl
 */
public class AnalysisScore {
    private final String id;
    private final String name;

    private final int totalImpact;

    private final int errorsSize;
    private final int highPrioritySize;
    private final int normalPrioritySize;
    private final int lowPrioritySize;
    private final int totalSize;

    public AnalysisScore(final String name, final AnalysisConfiguration configuration, final AnalysisResult result) {
        Ensure.that(name).isNotEmpty();
        Ensure.that(result.getId()).isNotEmpty();

        this.name = name;
        this.id = result.getId();

        this.errorsSize = result.getTotalErrorsSize();
        this.highPrioritySize = result.getTotalHighPrioritySize();
        this.normalPrioritySize = result.getTotalNormalPrioritySize();
        this.lowPrioritySize = result.getTotalLowPrioritySize();
        this.totalSize = result.getTotalSize();

        totalImpact = computeImpact(configuration);
    }

    private int computeImpact(final AnalysisConfiguration configuration) {
        int change = 0;

        change = change + configuration.getErrorImpact() * errorsSize;
        change = change + configuration.getHighImpact() * highPrioritySize;
        change = change + configuration.getNormalImpact() * normalPrioritySize;
        change = change + configuration.getLowImpact() * lowPrioritySize;

        return change;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalImpact() {
        return totalImpact;
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
}
