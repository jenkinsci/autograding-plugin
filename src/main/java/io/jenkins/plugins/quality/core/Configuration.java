package io.jenkins.plugins.quality.core;

/**
 * Stores all read Configurations in an object.
 *
 * @author Eva-Maria Zeintl
 */

public class Configuration {

    /**
     * Default configs.
     */
    private int dMaxScore;
    private String defaultCheck;
    private boolean dtoCheck;
    private String dkindOfGrading;
    private int weightError;
    private int weightHigh;
    private int weightNormal;
    private int weightLow;

    /**
     * PIT configs.
     */
    private int pMaxScore;
    private String pitCheck;
    private boolean ptoCheck;
    private String pkindOfGrading;
    private int weightMutations;
    private int weightUndetected;
    private int weightDetected;

    /**
     * code-coverage configs.
     */
    private int cMaxScore;
    private String cocoCheck;
    private boolean ctoCheck;
    private String ckindOfGrading;
    private int weightCovered;
    private int weightMissed;

    /**
     * junit configs.
     */
    private int jMaxScore;
    private String junitCheck;
    private boolean jtoCheck;
    private String jkindOfGrading;
    private int weightSkipped;
    private int weightFailures;
    private int totalTestsrun;
    private int weightPassed;

    public int getdMaxScore() {
        return dMaxScore;
    }

    public String getDefaultCheck() {
        return defaultCheck;
    }

    public boolean isDtoCheck() {
        return dtoCheck;
    }

    public String getDkindOfGrading() {
        return dkindOfGrading;
    }

    public int getWeightError() {
        return weightError;
    }

    public int getWeightHigh() {
        return weightHigh;
    }

    public int getWeightNormal() {
        return weightNormal;
    }

    public int getWeightLow() {
        return weightLow;
    }

    public int getpMaxScore() {
        return pMaxScore;
    }

    public String getPitCheck() {
        return pitCheck;
    }

    public boolean isPtoCheck() {
        return ptoCheck;
    }

    public String getPkindOfGrading() {
        return pkindOfGrading;
    }

    public int getWeightMutations() {
        return weightMutations;
    }

    public int getWeightUndetected() {
        return weightUndetected;
    }

    public int getWeightDetected() {
        return weightDetected;
    }

    public int getcMaxScore() {
        return cMaxScore;
    }

    public String getCocoCheck() {
        return cocoCheck;
    }

    public boolean isCtoCheck() {
        return ctoCheck;
    }

    public String getCkindOfGrading() {
        return ckindOfGrading;
    }

    public int getWeightCovered() {
        return weightCovered;
    }

    public int getWeightMissed() {
        return weightMissed;
    }

    public int getjMaxScore() {
        return jMaxScore;
    }

    public String getJunitCheck() {
        return junitCheck;
    }

    public boolean isJtoCheck() {
        return jtoCheck;
    }

    public String getJkindOfGrading() {
        return jkindOfGrading;
    }

    public int getWeightSkipped() {
        return weightSkipped;
    }

    public int getWeightFailures() {
        return weightFailures;
    }

    public int getTotalTestsrun() {
        return totalTestsrun;
    }

    public int getWeightPassed() {
        return weightPassed;
    }

    public int getMaxScore() {
        return dMaxScore + cMaxScore + jMaxScore + pMaxScore;
    }
}
