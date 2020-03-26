package io.jenkins.plugins.grading;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.util.LogHandler;

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

    public AnalysisScore(final String name, final AnalysisConfiguration analysisConfiguration,
            final AnalysisResult result, final LogHandler logHandler) {
        super();

        this.name = name;
        this.id = result.getId();

        this.errorsSize = result.getTotalErrorsSize();
        this.highPrioritySize = result.getTotalHighPrioritySize();
        this.normalPrioritySize = result.getTotalNormalPrioritySize();
        this.lowPrioritySize = result.getTotalLowPrioritySize();
        this.totalSize = result.getTotalSize();

        totalImpact = computeImpact(analysisConfiguration);

        logHandler.log("-> Score %d - from recorded warnings distribution of %d, %d, %d, %d",
                totalImpact, errorsSize, highPrioritySize, normalPrioritySize, lowPrioritySize);
    }

    private int computeImpact(final AnalysisConfiguration configs) {
        int change = 0;
        change = change + configs.getWeightError() * errorsSize;
        change = change + configs.getWeightHigh() * highPrioritySize;
        change = change + configs.getWeightNormal() * normalPrioritySize;
        change = change + configs.getWeightLow() * lowPrioritySize;

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
