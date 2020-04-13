package io.jenkins.plugins.grading;

/**
 * Base class for configurations with a maximum score.
 *
 * @author Ullrich Hafner
 */
public class Configuration {
    private int maxScore;

    Configuration(final int maxScore) {
        this.maxScore = maxScore;
    }

    public final void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Base class for builders of {@link Configuration} instances.
     *
     * @author Ullrich Hafner
     */
    static class ConfigurationBuilder {
        private int maxScore;

        /**
         * Sets the maximum score to achieve for the test results.
         *
         * @param maxScore
         *         maximum score to achieve for the test results.
         *
         * @return this
         */
        public ConfigurationBuilder setMaxScore(final int maxScore) {
            this.maxScore = maxScore;

            return this;
        }

        int getMaxScore() {
            return maxScore;
        }
    }
}
