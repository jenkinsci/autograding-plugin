package io.jenkins.plugins.grading;

public class AnalysisConfigurationBuilder {
    private int maxScore;
    private int weightError;
    private int weightHigh;
    private int weightNormal;
    private int weightLow;

    public AnalysisConfigurationBuilder setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
        return this;
    }

    public AnalysisConfigurationBuilder setWeightError(final int weightError) {
        this.weightError = weightError;
        return this;
    }

    public AnalysisConfigurationBuilder setWeightHigh(final int weightHigh) {
        this.weightHigh = weightHigh;
        return this;
    }

    public AnalysisConfigurationBuilder setWeightNormal(final int weightNormal) {
        this.weightNormal = weightNormal;
        return this;
    }

    public AnalysisConfigurationBuilder setWeightLow(final int weightLow) {
        this.weightLow = weightLow;
        return this;
    }

    public AnalysisConfiguration build() {
        return new AnalysisConfiguration(maxScore, weightError, weightHigh, weightNormal, weightLow);
    }
}
