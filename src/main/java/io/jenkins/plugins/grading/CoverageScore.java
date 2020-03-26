package io.jenkins.plugins.grading;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.model.TaskListener;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.util.LogHandler;

/**
 * takes {@link Configuration} and the results of code coverage.. Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class CoverageScore {

    private final String id;
    private int totalChange;

    //CodeCoverage
    private final int totalCovered;
    private final int totalLines;
    private final int ratio;
    private final int totalMissed;

    /**
     * Creates a new instance of {@link CoverageScore} for code coverage results.
     *
     * @param id
     *         the name of the check
     * @param totalCovered
     *         the total number of covered code
     * @param totalLines
     *         the total number of missed code
     * @param ratio
     *         the ratio of missed code
     */
    public CoverageScore(final String id, final int totalCovered, final int totalLines, final int ratio) {
        super();
        this.id = id;
        this.totalCovered = totalCovered;
        this.totalLines = totalLines;
        this.totalMissed = totalLines - totalCovered;
        this.ratio = 100 - ratio;
    }

    public CoverageScore(final CoverageConfiguration coverageConfiguration, final Ratio action,
            final LogHandler logHandler) {
        this("Line", (int) action.numerator, (int) action.denominator, action.getPercentage());

        totalChange = calc(coverageConfiguration);

        logHandler.log("-> Score %d - from recorded coverage results: %d, %d, %d, %d",
                totalChange, totalCovered, totalLines, totalMissed, ratio);
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
    public int calculate(final CoverageConfiguration configs, @NonNull final TaskListener listener) {
        int change = calc(configs);
        listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
        return change;
    }

    private int calc(final CoverageConfiguration configs) {
        int change = 0;
        change = change + configs.getWeightMissed() * ratio;

        totalChange = change;
        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalChange() {
        return totalChange;
    }

    public int getTotalCovered() {
        return totalCovered;
    }

    public int getTotalMissed() {
        return totalMissed;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getRatio() {
        return ratio;
    }

}
