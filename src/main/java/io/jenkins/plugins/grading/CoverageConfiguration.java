package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade static analysis results.
 *
 * @author Ullrich Hafner
 */
public class CoverageConfiguration {
    private int maxScore;

    private int weightCovered;
    private int weightMissed;

    public static CoverageConfiguration from(final JSONObject json) {
        return (CoverageConfiguration) JSONObject.toBean(json, CoverageConfiguration.class);
    }

    public CoverageConfiguration() {
        // empty constructor required for automatic Json conversion
    }

    public CoverageConfiguration(final int maxScore, final int weightCovered, final int weightMissed) {
        this();

        this.maxScore = maxScore;
        this.weightCovered = weightCovered;
        this.weightMissed = weightMissed;
    }

    public void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getWeightCovered() {
        return weightCovered;
    }

    public void setWeightCovered(final int weightCovered) {
        this.weightCovered = weightCovered;
    }

    public int getWeightMissed() {
        return weightMissed;
    }

    public void setWeightMissed(final int weightMissed) {
        this.weightMissed = weightMissed;
    }

    public static class CoverageConfigurationBuilder {
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
}
