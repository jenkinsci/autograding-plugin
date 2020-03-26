package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade static analysis results.
 *
 * @author Ullrich Hafner
 */
public class AnalysisConfiguration {
    private int maxScore;

    private int weightError;
    private int weightHigh;
    private int weightNormal;
    private int weightLow;

    public static AnalysisConfiguration from(final JSONObject json) {
        return (AnalysisConfiguration) JSONObject.toBean(json, AnalysisConfiguration.class);
    }

    public AnalysisConfiguration() {
        // empty constructor required for automatic Json conversion
    }

    public AnalysisConfiguration(final int maxScore,
            final int weightError, final int weightHigh, final int weightNormal, final int weightLow) {
        this();

        this.maxScore = maxScore;
        this.weightError = weightError;
        this.weightHigh = weightHigh;
        this.weightNormal = weightNormal;
        this.weightLow = weightLow;
    }

    public void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
    }

    public void setWeightError(final int weightError) {
        this.weightError = weightError;
    }

    public void setWeightHigh(final int weightHigh) {
        this.weightHigh = weightHigh;
    }

    public void setWeightNormal(final int weightNormal) {
        this.weightNormal = weightNormal;
    }

    public void setWeightLow(final int weightLow) {
        this.weightLow = weightLow;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getWeightError() {
        return weightError;
    }

    public int getWeightHigh() {
        return weightHigh;
    }

    public int getWeightNormal() {
        return weightNormal;
    }

    public int getWeightLow() {
        return weightLow;
    }

    public static class AnalysisConfigurationBuilder {
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
}
