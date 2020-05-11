package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import net.sf.json.JSONObject;

/**
 * Configuration to grade test results.
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("PMD.DataClass")
public class TestConfiguration extends Configuration {
    private static final long serialVersionUID = 1L;

    private int failureImpact;
    private int passedImpact;
    private int skippedImpact;

    /**
     * Converts the specified JSON object to a new instance if {@link TestConfiguration}.
     *
     * @param json
     *         the json object to convert
     *
     * @return the corresponding {@link TestConfiguration} instance
     */
    public static TestConfiguration from(final JSONObject json) {
        return (TestConfiguration) JSONObject.toBean(json, TestConfiguration.class);
    }

    /**
     * Creates a configuration that suppresses the grading.
     */
    @SuppressWarnings("unused") // Required for JSON conversion
    public TestConfiguration() {
        this(0, 0, 0, 0);
    }

    private TestConfiguration(final int maxScore,
            final int skippedImpact, final int failureImpact, final int passedImpact) {
        super(maxScore);

        this.passedImpact = passedImpact;
        this.failureImpact = failureImpact;
        this.skippedImpact = skippedImpact;
    }

    public int getSkippedImpact() {
        return skippedImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setSkippedImpact(final int skippedImpact) {
        this.skippedImpact = skippedImpact;
    }

    public int getFailureImpact() {
        return failureImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setFailureImpact(final int failureImpact) {
        this.failureImpact = failureImpact;
    }

    public int getPassedImpact() {
        return passedImpact;
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public void setPassedImpact(final int passedImpact) {
        this.passedImpact = passedImpact;
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
        TestConfiguration that = (TestConfiguration) o;
        return failureImpact == that.failureImpact
                && passedImpact == that.passedImpact
                && skippedImpact == that.skippedImpact;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), failureImpact, passedImpact, skippedImpact);
    }

    /**
     * Builder to create a {@link TestConfiguration} instance.
     */
    public static class TestConfigurationBuilder extends ConfigurationBuilder {
        private int failureImpact;
        private int passedImpact;
        private int skippedImpact;

        @Override
        public TestConfigurationBuilder setMaxScore(final int maxScore) {
            return (TestConfigurationBuilder) super.setMaxScore(maxScore);
        }

        /**
         * Sets the number of points to increase or decrease the score if a test is skipped.
         *
         * @param skippedImpact
         *         number of points to increase or decrease the score if a test is skipped
         *
         * @return this
         */
        public TestConfigurationBuilder setSkippedImpact(final int skippedImpact) {
            this.skippedImpact = skippedImpact;
            return this;
        }

        /**
         * Sets the number of points to increase or decrease the score if a test has failed.
         *
         * @param failureImpact
         *         number of points to increase or decrease the score if a test has failed
         *
         * @return this
         */
        public TestConfigurationBuilder setFailureImpact(final int failureImpact) {
            this.failureImpact = failureImpact;
            return this;
        }

        /**
         * Sets the number of points to increase or decrease the score if a test has passed.
         *
         * @param passedImpact
         *         number of points to increase or decrease the score if a test has passed
         *
         * @return this
         */
        public TestConfigurationBuilder setPassedImpact(final int passedImpact) {
            this.passedImpact = passedImpact;
            return this;
        }

        /**
         * Creates a new instance of {@link TestConfiguration} using the configured properties.
         *
         * @return the created instance
         */
        public TestConfiguration build() {
            return new TestConfiguration(getMaxScore(), skippedImpact, failureImpact, passedImpact);
        }
    }
}
