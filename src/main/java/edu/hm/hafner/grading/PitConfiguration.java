package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import net.sf.json.JSONObject;

/**
 * Configuration to grade mutation test results.
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("PMD.DataClass")
public class PitConfiguration extends Configuration {
    private static final long serialVersionUID = 1L;

    private int undetectedImpact;
    private int detectedImpact;
    private int undetectedPercentageImpact;
    private int detectedPercentageImpact;

    /**
     * Converts the specified JSON object to a new instance if {@link PitConfiguration}.
     *
     * @param json
     *         the json object to convert
     *
     * @return the corresponding {@link PitConfiguration} instance
     */
    public static PitConfiguration from(final JSONObject json) {
        return (PitConfiguration) JSONObject.toBean(json, PitConfiguration.class);
    }

    /**
     * Creates a configuration that suppresses the grading.
     */
    @SuppressWarnings("unused") // Required for JSON conversion
    public PitConfiguration() {
        this(0, 0, 0, 0, 0);
    }

    private PitConfiguration(final int maxScore,
            final int undetectedImpact, final int detectedImpact, 
            final int undetectedPercentageImpact, final int detectedPercentageImpact) {
        super(maxScore);

        this.undetectedImpact = undetectedImpact;
        this.detectedImpact = detectedImpact;
        this.undetectedPercentageImpact = undetectedPercentageImpact;
        this.detectedPercentageImpact = detectedPercentageImpact;
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

    public int getDetectedPercentageImpact() {
        return detectedPercentageImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setDetectedPercentageImpact(final int detectedPercentageImpact) {
        this.detectedPercentageImpact = detectedPercentageImpact;
    }

    public int getUndetectedPercentageImpact() {
        return undetectedPercentageImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setUndetectedPercentageImpact(final int undetectedPercentageImpact) {
        this.undetectedPercentageImpact = undetectedPercentageImpact;
    }

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PitConfiguration that = (PitConfiguration) o;
        return detectedPercentageImpact == that.detectedPercentageImpact
                && undetectedPercentageImpact == that.undetectedPercentageImpact
                && detectedImpact == that.detectedImpact
                && undetectedImpact == that.undetectedImpact;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), detectedPercentageImpact, undetectedPercentageImpact, detectedImpact, undetectedImpact);
    }

    /**
     * Builder to create a {@link PitConfiguration} instance.
     */
    public static class PitConfigurationBuilder extends ConfigurationBuilder {
        private int undetectedImpact = 0;
        private int detectedImpact = 0;
        private int undetectedPercentageImpact = 0;
        private int detectedPercentageImpact = 0;

        @Override
        public PitConfigurationBuilder setMaxScore(final int maxScore) {
            return (PitConfigurationBuilder) super.setMaxScore(maxScore);
        }

        /**
         * Sets the number of points to decrease the score for each undetected mutation.
         *
         * @param undetectedImpact
         *         the number of points to decrease the score for each undetected mutation.
         *
         * @return this
         */
        public PitConfigurationBuilder setUndetectedImpact(final int undetectedImpact) {
            this.undetectedImpact = undetectedImpact;
            return this;
        }

        /**
         * Sets the number of points to increase the score for each detected mutation.
         *
         * @param detectedImpact
         *         the number of points to increase the score for each detected mutation.
         *
         * @return this
         */
        public PitConfigurationBuilder setDetectedImpact(final int detectedImpact) {
            this.detectedImpact = detectedImpact;
            return this;
        }

        /**
         * Sets the number of points to decrease the score for each missed coverage percentage point.
         *
         * @param undetectedPercentageImpact
         *         the number of points to decrease the score for each missed coverage percentage point.
         *
         * @return this
         */
        public PitConfigurationBuilder setUndetectedPercentageImpact(final int undetectedPercentageImpact) {
            this.undetectedPercentageImpact = undetectedPercentageImpact;
            return this;
        }

        /**
         * Sets the number of points to increase the score for each detected coverage percentage point.
         *
         * @param detectedPercentageImpact
         *         the number of points to increase the score for each detected coverage percentage point.
         *
         * @return this
         */
        public PitConfigurationBuilder setDetectedPercentageImpact(final int detectedPercentageImpact) {
            this.detectedPercentageImpact = detectedPercentageImpact;
            return this;
        }

        /**
         * Creates a new instance of {@link PitConfiguration} using the configured properties.
         *
         * @return the created instance
         */
        public PitConfiguration build() {
            return new PitConfiguration(getMaxScore(), undetectedImpact, detectedImpact,
                    undetectedPercentageImpact, detectedPercentageImpact);
        }
    }
}
