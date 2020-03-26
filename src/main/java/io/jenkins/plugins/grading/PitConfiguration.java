package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade mutation test results.
 *
 * @author Ullrich Hafner
 */
public class PitConfiguration extends Configuration {
    private int ratioImpact;
    private int detectedImpact;
    private int undetectedImpact;

    public static PitConfiguration from(final JSONObject json) {
        return (PitConfiguration) JSONObject.toBean(json, PitConfiguration.class);
    }

    public PitConfiguration(final int maxScore,
            final int undetectedImpact, final int detectedImpact, final int ratioImpact) {
        super(maxScore);

        this.undetectedImpact = undetectedImpact;
        this.detectedImpact = detectedImpact;
        this.ratioImpact = ratioImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public PitConfiguration() {
        this(0, 0, 0, 0);
    }

    public int getUndetectedImpact() {
        return undetectedImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setUndetectedImpact(final int undetectedImpact) {
        this.undetectedImpact = undetectedImpact;
    }

    public int getDetectedImpact() {
        return detectedImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setDetectedImpact(final int detectedImpact) {
        this.detectedImpact = detectedImpact;
    }

    public int getRatioImpact() {
        return ratioImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setRatioImpact(final int ratioImpact) {
        this.ratioImpact = ratioImpact;
    }

    public static class PitConfigurationBuilder {
        private int maxScore = 0;
        private int undetectedImpact = 0;
        private int detectedImpact = 0;
        private int ratioImpact = 0;

        public PitConfigurationBuilder setMaxScore(final int maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public PitConfigurationBuilder setUndetectedImpact(final int undetectedImpact) {
            this.undetectedImpact = undetectedImpact;
            return this;
        }

        public PitConfigurationBuilder setDetectedImpact(final int detectedImpact) {
            this.detectedImpact = detectedImpact;
            return this;
        }

        public PitConfigurationBuilder setRatioImpact(final int ratioImpact) {
            this.ratioImpact = ratioImpact;
            return this;
        }

        public PitConfiguration build() {
            return new PitConfiguration(maxScore, undetectedImpact, detectedImpact, ratioImpact);
        }
    }
}
