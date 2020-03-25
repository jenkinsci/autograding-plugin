package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * FIXME: comment class.
 *
 * @author Ullrich Hafner
 */
public class TestsConfiguration {
    private int maxScore;
    private int weightSkipped;
    private int weightFailures;
    private int weightPassed;

    public static TestsConfiguration from(final JSONObject json) {
        return (TestsConfiguration) JSONObject.toBean(json, TestsConfiguration.class);
    }

    public TestsConfiguration() {

    }

    public TestsConfiguration(final int maxScore, final int weightSkipped, final int weightFailures,
            final int weightPassed) {
        this();
        this.maxScore = maxScore;
        this.weightSkipped = weightSkipped;
        this.weightFailures = weightFailures;
        this.weightPassed = weightPassed;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
    }

    public int getWeightSkipped() {
        return weightSkipped;
    }

    public void setWeightSkipped(final int weightSkipped) {
        this.weightSkipped = weightSkipped;
    }

    public int getWeightFailures() {
        return weightFailures;
    }

    public void setWeightFailures(final int weightFailures) {
        this.weightFailures = weightFailures;
    }

    public int getWeightPassed() {
        return weightPassed;
    }

    public void setWeightPassed(final int weightPassed) {
        this.weightPassed = weightPassed;
    }
}
