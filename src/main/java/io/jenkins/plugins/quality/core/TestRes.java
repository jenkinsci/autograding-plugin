package io.jenkins.plugins.quality.core;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of Junit tests.
 * Calculates and updates quality score
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
     * @param id           the name of the check
     * @param totalPassed  the total number of passed tests
     * @param totalRun     the total number of run tests
     * @param totalFailed  the total number of failed tests
     * @param totalSkipped the total number of skipped tests
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

    /**
     * Calculates and saves new {@link Score}.
     *
     * @param configs  all Configurations
     * @param listener Console log
     * @return returns the delta that has been changed in score
     */
    public int calculate(final Configuration configs, @NonNull final TaskListener listener) {
        int change = 0;
        if (configs.isJtoCheck()) {
            change = change + configs.getWeightPassed() * totalPassed;
            change = change + configs.getWeightFailures() * totalFailed;
            change = change + configs.getWeightSkipped() * totalSkipped;

            listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
            setTotalChange(change);
            return change;
        }
        return change;
    }

    public void setTotalChange(final int totalChange) {
        this.totalChange = totalChange;
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
