package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade code coverage results.
 *
 * @author Ullrich Hafner
 */
public class CoverageConfiguration extends Configuration {
    private int coveredImpact;
    private int missedImpact;

    public static CoverageConfiguration from(final JSONObject json) {
        return (CoverageConfiguration) JSONObject.toBean(json, CoverageConfiguration.class);
    }

    public CoverageConfiguration() {
        this(0, 0, 0);
    }

    public CoverageConfiguration(final int maxScore,
            final int coveredImpact, final int missedImpact) {
        super(maxScore);

        this.coveredImpact = coveredImpact;
        this.missedImpact = missedImpact;
    }

    public int getCoveredImpact() {
        return coveredImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setCoveredImpact(final int coveredImpact) {
        this.coveredImpact = coveredImpact;
    }

    public int getMissedImpact() {
        return missedImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setMissedImpact(final int missedImpact) {
        this.missedImpact = missedImpact;
    }

    public static class CoverageConfigurationBuilder {
        private int maxScore = 0;
        private int coveredImpact = 0;
        private int missedImpact = 0;

        public CoverageConfigurationBuilder setMaxScore(final int maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public CoverageConfigurationBuilder setCoveredImpact(final int coveredImpact) {
            this.coveredImpact = coveredImpact;
            return this;
        }

        public CoverageConfigurationBuilder setMissedImpact(final int missedImpact) {
            this.missedImpact = missedImpact;
            return this;
        }

        public CoverageConfiguration build() {
            return new CoverageConfiguration(maxScore, coveredImpact, missedImpact);
        }
    }
}
