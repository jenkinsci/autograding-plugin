package io.jenkins.plugins.grading;

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

    private int coveredImpact;
    private int missedImpact;

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
        return coveredImpact == that.coveredImpact && missedImpact == that.missedImpact;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), coveredImpact, missedImpact);
    }

    /**
     * Builder to create a {@link CoverageConfiguration} instance.
     */
    public static class CoverageConfigurationBuilder extends ConfigurationBuilder {
        private int coveredImpact = 0;
        private int missedImpact = 0;

        @Override
        public CoverageConfigurationBuilder setMaxScore(final int maxScore) {
            return (CoverageConfigurationBuilder) super.setMaxScore(maxScore);
        }

        /**
         * Sets the number of points to increase the score for each coverage percentage point.
         *
         * @param coveredImpact
         *         the number of points to increase the score for each coverage percentage point.
         *
         * @return this
         */
        public CoverageConfigurationBuilder setCoveredImpact(final int coveredImpact) {
            this.coveredImpact = coveredImpact;
            return this;
        }

        /**
         * Sets the number of points to decrease the score for each missing coverage percentage point.
         *
         * @param missedImpact
         *         the number of points to decrease the score for each missing coverage percentage point.
         *
         * @return this
         */
        public CoverageConfigurationBuilder setMissedImpact(final int missedImpact) {
            this.missedImpact = missedImpact;
            return this;
        }

        /**
         * Creates a new instance of {@link CoverageConfiguration} using the configured properties.
         *
         * @return the created instance
         */
        public CoverageConfiguration build() {
            return new CoverageConfiguration(getMaxScore(), coveredImpact, missedImpact);
        }
    }
}
