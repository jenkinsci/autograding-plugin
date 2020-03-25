package io.jenkins.plugins.grading;

import net.sf.json.JSONObject;

/**
 * Configuration to grade static analysis results.
 *
 * @author Ullrich Hafner
 */
public class PitConfiguration {
    private int maxScore;

    private int weightUndetected;
    private int weightDetected;

    public static PitConfiguration from(final JSONObject json) {
        return (PitConfiguration) JSONObject.toBean(json, PitConfiguration.class);
    }

    public PitConfiguration() {
    }

    public PitConfiguration(final int maxScore, final int weightUndetected, final int weightDetected) {
        this();

        this.maxScore = maxScore;
        this.weightUndetected = weightUndetected;
        this.weightDetected = weightDetected;
    }

    public void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getWeightUndetected() {
        return weightUndetected;
    }

    public void setWeightUndetected(final int weightUndetected) {
        this.weightUndetected = weightUndetected;
    }

    public int getWeightDetected() {
        return weightDetected;
    }

    public void setWeightDetected(final int weightDetected) {
        this.weightDetected = weightDetected;
    }
}
