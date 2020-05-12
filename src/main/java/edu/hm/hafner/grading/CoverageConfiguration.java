package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import net.sf.json.JSONObject;

/**
 * Configuration to grade code coverage results.
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("PMD.DataClass")
public class CoverageConfiguration extends Configuration {
    private static final long serialVersionUID = 1L;

    private int coveredPercentageImpact;
    private int missedPercentageImpact;

    /**
     * Converts the specified JSON object to a new instance if {@link CoverageConfiguration}.
     *
     * @param json
     *         the json object to convert
     *
     * @return the corresponding {@link CoverageConfiguration} instance
     */
    public static CoverageConfiguration from(final JSONObject json) {
        return (CoverageConfiguration) JSONObject.toBean(json, CoverageConfiguration.class);
    }

    /**
     * Creates a configuration that suppresses the grading.
     */
    public CoverageConfiguration() {
        this(0, 0, 0);
    }

    private CoverageConfiguration(final int maxScore,
            final int coveredPercentageImpact, final int missedPercentageImpact) {
        super(maxScore);

        this.coveredPercentageImpact = coveredPercentageImpact;
        this.missedPercentageImpact = missedPercentageImpact;
    }

    public int getCoveredPercentageImpact() {
        return coveredPercentageImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setCoveredPercentageImpact(final int coveredPercentageImpact) {
        this.coveredPercentageImpact = coveredPercentageImpact;
    }

    public int getMissedPercentageImpact() {
        return missedPercentageImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setMissedPercentageImpact(final int missedPercentageImpact) {
        this.missedPercentageImpact = missedPercentageImpact;
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
        CoverageConfiguration that = (CoverageConfiguration) o;
        return coveredPercentageImpact == that.coveredPercentageImpact
                && missedPercentageImpact == that.missedPercentageImpact;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), coveredPercentageImpact, missedPercentageImpact);
    }

    /**
     * Builder to create a {@link CoverageConfiguration} instance.
     */
    public static class CoverageConfigurationBuilder extends ConfigurationBuilder {
        private int coveredPercentageImpact = 0;
        private int missedPercentageImpact = 0;

        @Override
        public CoverageConfigurationBuilder setMaxScore(final int maxScore) {
            return (CoverageConfigurationBuilder) super.setMaxScore(maxScore);
        }

        /**
         * Sets the number of points to increase the score for each coverage percentage point.
         *
         * @param coveredPercentageImpact
         *         the number of points to increase the score for each coverage percentage point.
         *
         * @return this
         */
        public CoverageConfigurationBuilder setCoveredPercentageImpact(final int coveredPercentageImpact) {
            this.coveredPercentageImpact = coveredPercentageImpact;
            return this;
        }

        /**
         * Sets the number of points to decrease the score for each missing coverage percentage point.
         *
         * @param missedPercentageImpact
         *         the number of points to decrease the score for each missing coverage percentage point.
         *
         * @return this
         */
        public CoverageConfigurationBuilder setMissedPercentageImpact(final int missedPercentageImpact) {
            this.missedPercentageImpact = missedPercentageImpact;
            return this;
        }

        /**
         * Creates a new instance of {@link CoverageConfiguration} using the configured properties.
         *
         * @return the created instance
         */
        public CoverageConfiguration build() {
            return new CoverageConfiguration(getMaxScore(), coveredPercentageImpact, missedPercentageImpact);
        }
    }
}
