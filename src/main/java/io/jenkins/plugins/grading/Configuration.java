package io.jenkins.plugins.grading;

/**
 * Base class for configurations with a maximum score.
 *
 * @author Ullrich Hafner
 */
class Configuration {
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
}
