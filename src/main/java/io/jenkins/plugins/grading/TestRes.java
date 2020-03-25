package io.jenkins.plugins.grading;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.model.TaskListener;
import hudson.tasks.junit.TestResultAction;

import io.jenkins.plugins.util.LogHandler;

/**
 * takes {@link Configuration} and the results of Junit tests. Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class TestRes {

    private final String id;
    private int totalChange;

    //Junit
    private final int totalPassed;
    private final int totalRun;
    private final int totalFailed;
    private final int totalSkipped;

    /**
     * Creates a new instance of {@link TestRes} for Junit results.
     *
     * @param id
     *         the name of the check
     * @param totalPassed
     *         the total number of passed tests
     * @param totalRun
     *         the total number of run tests
     * @param totalFailed
     *         the total number of failed tests
     * @param totalSkipped
     *         the total number of skipped tests
     */
    public TestRes(final String id, final int totalPassed, final int totalRun, final int totalFailed,
            final int totalSkipped) {
        super();
        this.id = id;
        this.totalPassed = totalPassed;
        this.totalFailed = totalFailed;
        this.totalRun = totalRun;
        this.totalSkipped = totalSkipped;
    }

    public TestRes(final TestsConfiguration testsConfiguration, final TestResultAction action,
            final LogHandler logHandler) {
        this(action.getDisplayName(), action.getResult().getPassCount(), action.getTotalCount(),
                action.getResult().getFailCount(), action.getResult().getSkipCount());
        totalChange = calc(testsConfiguration);

        logHandler.log("-> Score %d - from recorded test results: %d, %d, %d, %d",
                totalChange, totalRun, totalPassed, totalFailed, totalSkipped);
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
    public int calculate(final TestsConfiguration configs, @NonNull final TaskListener listener) {
        int change = calc(configs);
        listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
        return change;
    }

    private int calc(final TestsConfiguration configs) {
        int change = 0;
        change = change + configs.getWeightPassed() * totalPassed;
        change = change + configs.getWeightFailures() * totalFailed;
        change = change + configs.getWeightSkipped() * totalSkipped;

        totalChange = change;
        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalChange() {
        return totalChange;
    }

    public int getTotalPassed() {
        return totalPassed;
    }

    public int getTotalRun() {
        return totalRun;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

}
