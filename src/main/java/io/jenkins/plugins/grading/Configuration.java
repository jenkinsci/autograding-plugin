package io.jenkins.plugins.grading;

import java.io.Serializable;
import java.util.Objects;

import edu.hm.hafner.util.Generated;

/**
 * Base class for configurations with a maximum score.
 *
 * @author Ullrich Hafner
 */
public class Configuration implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Configuration that = (Configuration) o;
        return maxScore == that.maxScore;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(maxScore);
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
