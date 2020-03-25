package io.jenkins.plugins.grading;

public class PitConfigurationBuilder {
    private int maxScore;
    private int weightUndetected;
    private int weightDetected;

    public PitConfigurationBuilder setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
        return this;
    }

    public PitConfigurationBuilder setWeightUndetected(final int weightUndetected) {
        this.weightUndetected = weightUndetected;
        return this;
    }

    public PitConfigurationBuilder setWeightDetected(final int weightDetected) {
        this.weightDetected = weightDetected;
        return this;
    }

    public PitConfiguration build() {
        return new PitConfiguration(maxScore, weightUndetected, weightDetected);
    }
}
