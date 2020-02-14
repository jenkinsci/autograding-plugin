package io.jenkins.plugins.quality.core;

import edu.hm.hafner.util.VisibleForTesting;

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
    private int weightUndetected;
    private int weightDetected;

    /**
     * code-coverage configs.
     */
    private int cMaxScore;
    private String cocoCheck;
    private boolean ctoCheck;
    private int weightCovered;
    private int weightMissed;

    /**
     * junit configs.
     */
    private int jMaxScore;
    private String junitCheck;
    private boolean jtoCheck;
    private int weightSkipped;
    private int weightFailures;
    private int weightPassed;

    public Configuration(){};

    @VisibleForTesting
    Configuration(final int dMaxScore, final String aDefault, final boolean dtoCheck,
                         final int weightError, final int weightHigh, final int weightNormal, final int weightLow,
                         final int pMaxScore, final String pit, final boolean ptoCheck,
                         final int weightUndetected, final int weightDetected,
                         final int cMaxScore, final String coco, final boolean ctoCheck,
                         final int weightCovered, final int weightMissed,
                         final int jMaxScore, final String junit, final boolean jtoCheck,
                         final int weightSkipped, final int weightFailures, final int weightPassed) {
        this.dMaxScore = dMaxScore;
        this.defaultCheck = aDefault;
        this.dtoCheck = dtoCheck;
        this.weightError = weightError;
        this.weightHigh = weightHigh;
        this.weightNormal = weightNormal;
        this.weightLow = weightLow;
        this.pMaxScore = pMaxScore;
        this.pitCheck = pit;
        this.ptoCheck = ptoCheck;
        this.weightUndetected = weightUndetected;
        this.weightDetected = weightDetected;
        this.cMaxScore = cMaxScore;
        this.cocoCheck = coco;
        this.ctoCheck = ctoCheck;
        this.weightCovered = weightCovered;
        this.weightMissed = weightMissed;
        this.jMaxScore = jMaxScore;
        this.junitCheck = junit;
        this.jtoCheck = jtoCheck;
        this.weightSkipped = weightSkipped;
        this.weightFailures = weightFailures;
        this.weightPassed = weightPassed;
    }

    /**
     * The maximal achievable score for static checks.
     * @return dMaxScore Maximum for DefaultChecks
      */
    public int getdMaxScore() {
        return dMaxScore;
    }

    public String getDefaultCheck() {
        return defaultCheck;
    }

    public boolean isDtoCheck() {
        return dtoCheck;
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

    /**
     * The maximal achievable score for pitmutation checks.
     * @return pMaxScore Maximum for pitmutations
     */
    public int getpMaxScore() {
        return pMaxScore;
    }

    public String getPitCheck() {
        return pitCheck;
    }

    public boolean isPtoCheck() {
        return ptoCheck;
    }

    public int getWeightUndetected() {
        return weightUndetected;
    }

    public int getWeightDetected() {
        return weightDetected;
    }

    /**
     * The maximal achievable score for code coverage checks.
     * @return cMaxScore Maximum for Code Coverage
     */
    public int getcMaxScore() {
        return cMaxScore;
    }

    public String getCocoCheck() {
        return cocoCheck;
    }

    public boolean isCtoCheck() {
        return ctoCheck;
    }

    public int getWeightCovered() {
        return weightCovered;
    }

    public int getWeightMissed() {
        return weightMissed;
    }

    /**
     * The maximal achievable score for junit tests.
     * @return jMaxScore Maximum of JUNIT tests
     */
    public int getjMaxScore() {
        return jMaxScore;
    }

    public String getJunitCheck() {
        return junitCheck;
    }

    public boolean isJtoCheck() {
        return jtoCheck;
    }

    public int getWeightSkipped() {
        return weightSkipped;
    }

    public int getWeightFailures() {
        return weightFailures;
    }

    public int getWeightPassed() {
        return weightPassed;
    }

    public int getMaxScore() {
        return dMaxScore + cMaxScore + jMaxScore + pMaxScore;
    }
}
