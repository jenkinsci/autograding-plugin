package io.jenkins.plugins.grading;

public class TestsConfigurationBuilder {
    private int maxScore;
    private int weightSkipped;
    private int weightFailures;
    private int weightPassed;

    public TestsConfigurationBuilder setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
        return this;
    }

    public TestsConfigurationBuilder setWeightSkipped(final int weightSkipped) {
        this.weightSkipped = weightSkipped;
        return this;
    }

    public TestsConfigurationBuilder setWeightFailures(final int weightFailures) {
        this.weightFailures = weightFailures;
        return this;
    }

    public TestsConfigurationBuilder setWeightPassed(final int weightPassed) {
        this.weightPassed = weightPassed;
        return this;
    }

    public TestsConfiguration build() {
        return new TestsConfiguration(maxScore, weightSkipped, weightFailures, weightPassed);
    }
}
