package io.jenkins.plugins.grading;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.model.TaskListener;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.util.LogHandler;

/**
 * takes {@link Configuration} and the results of default checks. Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class AnalysisScore {
    private final String id;
    private int totalChange;

    private final int totalErrors;
    private final int totalHighs;
    private final int totalNormals;
    private final int totalLows;
    private final int sum;

    /**
     * Creates a new instance of {@link AnalysisScore} for Default Checks.
     *
     * @param id
     *         the name of the check
     * @param totalErrors
     *         the total number of errors
     * @param totalHighs
     *         the total number of High issues
     * @param totalNormals
     *         the total number of Normal issues
     * @param totalLows
     *         the total number of Low issues
     * @param sum
     *         the total number of all issues
     */
    public AnalysisScore(final String id, final int totalErrors, final int totalHighs, final int totalNormals,
            final int totalLows, final int sum) {
        super();

        this.id = id;
        this.totalErrors = totalErrors;
        this.totalHighs = totalHighs;
        this.totalNormals = totalNormals;
        this.totalLows = totalLows;
        this.sum = sum;
    }

    public AnalysisScore(final String id, final int totalErrors, final int totalHighs, final int totalNormals,
            final int totalLows) {
        this(id, totalErrors, totalHighs, totalNormals, totalLows, totalLows + totalErrors + totalHighs + totalNormals + totalLows);
    }

    public AnalysisScore(final AnalysisConfiguration analysisConfiguration, final AnalysisResult result,
            final LogHandler logHandler) {
        this(result.getId(), result.getTotalErrorsSize(), result.getTotalHighPrioritySize(), result.getTotalNormalPrioritySize(), result.getTotalLowPrioritySize());

        totalChange = calc(analysisConfiguration);

        logHandler.log("-> Score %d - from recorded warnings distribution of %d, %d, %d, %d",
                totalChange, totalErrors, totalHighs, totalNormals, totalLows);
    }

    /**
     * Calculates and saves new {@link Score}.
     *
     * @param configs
     *         all Configurations
     * @param listener
     *         Console log
     *
     * @return returns the delta that has been changed in score
     */
    public int calculate(final AnalysisConfiguration configs, @NonNull final TaskListener listener) {
        int change = calc(configs);
        listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
        return change;
    }

    private int calc(final AnalysisConfiguration configs) {
        int change = 0;
        change = change + configs.getWeightError() * totalErrors;
        change = change + configs.getWeightHigh() * totalHighs;
        change = change + configs.getWeightNormal() * totalNormals;
        change = change + configs.getWeightLow() * totalLows;

        totalChange = change;
        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalChange() {
        return totalChange;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public int getTotalHighs() {
        return totalHighs;
    }

    public int getTotalNormals() {
        return totalNormals;
    }

    public int getTotalLows() {
        return totalLows;
    }

    public int getSum() {
        return sum;
    }
}
