package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of Junit tests.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class TestRes {

    private String id;
    private int totalChange;

    //Junit
    private int totalPassed;
    private int totalRun;
    private int totalFailed;
    private int totalSkipped;

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
     * @param base     All instances of test results
     * @param score    Score Object
     * @param listener Console log
     */
    public int calculate(final Configuration configs, TestRes base, Score score, TaskListener listener) {
        int change = 0;
        if (configs.isJtoCheck()) {
            change = change + configs.getWeightPassed() * base.getTotalPassed();
            change = change + configs.getWeightFailures() * base.getTotalFailed();
            change = change + configs.getWeightSkipped() * base.getTotalSkipped();

            if (configs.getJkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] " + base.getId() + " changed score by: " + change);
                score.addToScore(change);
            }
        }
        return change;
    }

    public void setTotalChange(int totalChange) {
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
