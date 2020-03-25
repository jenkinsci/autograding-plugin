package io.jenkins.plugins.grading;

public class CoverageConfigurationBuilder {
    private int maxScore;
    private int weightCovered;
    private int weightMissed;

    public CoverageConfigurationBuilder setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
        return this;
    }

    public CoverageConfigurationBuilder setWeightCovered(final int weightCovered) {
        this.weightCovered = weightCovered;
        return this;
    }

    public CoverageConfigurationBuilder setWeightMissed(final int weightMissed) {
        this.weightMissed = weightMissed;
        return this;
    }

    public CoverageConfiguration build() {
        return new CoverageConfiguration(maxScore, weightCovered, weightMissed);
    }
}
