package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade test results.
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("PMD.DataClass")
public class TestConfiguration extends Configuration {
    private int failureImpact;
    private int passedImpact;
    private int skippedImpact;

    public static TestConfiguration from(final JSONObject json) {
        return (TestConfiguration) JSONObject.toBean(json, TestConfiguration.class);
    }

    @SuppressWarnings("unused") // Required for JSON conversion
    public TestConfiguration() {
        this(0, 0, 0, 0);
    }

    public TestConfiguration(final int maxScore, final int skippedImpact, final int failureImpact,
            final int passedImpact) {
        super(maxScore);

        this.failureImpact = failureImpact;
        this.passedImpact = passedImpact;
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

    public static class TestConfigurationBuilder {
        private int maxScore;

        private int failureImpact;
        private int passedImpact;
        private int skippedImpact;

        public TestConfigurationBuilder setMaxScore(final int maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public TestConfigurationBuilder setSkippedImpact(final int skippedImpact) {
            this.skippedImpact = skippedImpact;
            return this;
        }

        public TestConfigurationBuilder setFailureImpact(final int failureImpact) {
            this.failureImpact = failureImpact;
            return this;
        }

        public TestConfigurationBuilder setPassedImpact(final int passedImpact) {
            this.passedImpact = passedImpact;
            return this;
        }

        public TestConfiguration build() {
            return new TestConfiguration(maxScore, skippedImpact, failureImpact, passedImpact);
        }
    }
}
