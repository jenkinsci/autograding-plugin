package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade static analysis results.
 *
 * @author Ullrich Hafner
 */
public class AnalysisConfiguration extends Configuration {
    private int errorImpact;
    private int highImpact;
    private int normalImpact;
    private int lowImpact;

    public static AnalysisConfiguration from(final JSONObject json) {
        return (AnalysisConfiguration) JSONObject.toBean(json, AnalysisConfiguration.class);
    }

    public AnalysisConfiguration() {
        this(0, 0, 0, 0, 0);
    }

    public AnalysisConfiguration(final int maxScore,
            final int errorImpact, final int highImpact, final int normalImpact, final int lowImpact) {
        super(maxScore);

        this.errorImpact = errorImpact;
        this.highImpact = highImpact;
        this.normalImpact = normalImpact;
        this.lowImpact = lowImpact;
    }

    public int getErrorImpact() {
        return errorImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setErrorImpact(final int errorImpact) {
        this.errorImpact = errorImpact;
    }

    public int getHighImpact() {
        return highImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setHighImpact(final int highImpact) {
        this.highImpact = highImpact;
    }

    public int getNormalImpact() {
        return normalImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setNormalImpact(final int weightNormal) {
        this.normalImpact = weightNormal;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setLowImpact(final int lowImpact) {
        this.lowImpact = lowImpact;
    }

    public int getLowImpact() {
        return lowImpact;
    }

    public static class AnalysisConfigurationBuilder {
        private int maxScore;

        private int errorImpact;
        private int highImpact;
        private int normalImpact;
        private int lowImpact;

        public AnalysisConfigurationBuilder setMaxScore(final int maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public AnalysisConfigurationBuilder setErrorImpact(final int errorImpact) {
            this.errorImpact = errorImpact;
            return this;
        }

        public AnalysisConfigurationBuilder setHighImpact(final int highImpact) {
            this.highImpact = highImpact;
            return this;
        }

        public AnalysisConfigurationBuilder setNormalImpact(final int normalImpact) {
            this.normalImpact = normalImpact;
            return this;
        }

        public AnalysisConfigurationBuilder setWeightLow(final int weightLow) {
            this.lowImpact = weightLow;
            return this;
        }

        public AnalysisConfiguration build() {
            return new AnalysisConfiguration(maxScore, errorImpact, highImpact, normalImpact, lowImpact);
        }
    }
}
