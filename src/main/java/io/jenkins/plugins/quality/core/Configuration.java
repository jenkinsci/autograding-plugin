package io.jenkins.plugins.quality.core;


public class Configuration {

    private int maxScore;
    /**
     * Default configs
     */
    private String defaultCheck;
    private boolean dtoCheck;
    private String dkindOfGrading;
    private int weightError;
    private int weightHigh;
    private int weightNormal;
    private int weightLow;

    /**
     * PIT configs
     */
    private String pitCheck;
    private boolean ptoCheck;
    private String pkindOfGrading;
    private int weightNoCoverage;
    private int weightRunError;
    private int weightStarted;
    private int weightNotStarted;
    private int weightMemoryError;
    private int weightNonViable;
    private int weightTimedOut;
    private int weightSurvived;
    private int weightKilled;

    /**
     * code-coverage configs
     */
    private String cocoCheck;
    private boolean ctoCheck;
    private String ckindOfGrading;
    private int weightCovered;
    private int weightMissed;

    /**
     * junit configs
     */
    private String junitCheck;
    private boolean jtoCheck;
    private String jkindOfGrading;
    private int weightSkipped;
    private int weightfailures;
    private int totalTestsrun;
    private int weightPassed;


    /*//config for static analysis
    public Configuration(String id, boolean toCheck, String kindOfGrading, int weightError, int weightHigh, int weightNormal, int weightLow) {
        super();
        this.id = id;
        this.toCheck = toCheck;
        this.kindOfGrading = kindOfGrading;
        this.weightError = weightError;
        this.weightHigh = weightHigh;
        this.weightNormal = weightNormal;
        this.weightLow = weightLow;
    }

    //config for Junit Tests
    public Configuration(String id, boolean jtoCheck, String jkindOfGrading, int jtotalTestsrun, int jweightfailures, int jweightPassed, int jweightSkipped, int j) {
        super();
        this.id = id;
        this.toCheck = jtoCheck;
        this.kindOfGrading = jkindOfGrading;
        this.jtotalTestsrun = jtotalTestsrun;
        this.jweightfailures = jweightfailures;
        this.jweightPassed = jweightPassed;
        this.jweightSkipped = jweightSkipped;
    }

    //config for code coverage
    public Configuration(String id, boolean toCheck, String kindOfGrading, int weightMissed, int weightCovered) {
        super();
        this.id = id;
        this.toCheck = toCheck;
        this.kindOfGrading = kindOfGrading;
        this.weightMissed = weightMissed;
        this.weightCovered = weightCovered;
    }

    //config for PIT Mutation
    public Configuration(String id, boolean toCheck, String kindOfGrading, int weightKilled, int weightSurvived,
                         int weightTimedOut, int weightNonViable, int weightMemoryError, int weightNotStarted,
                         int weightStarted, int weightRunError, int weightNoCoverage) {
        super();
        this.id = id;
        this.toCheck = toCheck;
        this.kindOfGrading = kindOfGrading;
        this.weightKilled = weightKilled;
        this.weightSurvived = weightSurvived;
        this.weightTimedOut = weightTimedOut;
        this.weightNonViable = weightNonViable;
        this.weightMemoryError = weightMemoryError;
        this.weightNotStarted = weightNotStarted;
        this.weightStarted = weightStarted;
        this.weightRunError = weightRunError;
        this.weightNoCoverage = weightNoCoverage;
    }*/

    public String getDefaultCheck() {
        return defaultCheck;
    }

    public int getMaxScore() {
        return maxScore;
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

    public String getPitCheck() {
        return pitCheck;
    }

    public boolean isPtoCheck() {
        return ptoCheck;
    }

    public String getPkindOfGrading() {
        return pkindOfGrading;
    }

    public int getWeightNoCoverage() {
        return weightNoCoverage;
    }

    public int getWeightRunError() {
        return weightRunError;
    }

    public int getWeightStarted() {
        return weightStarted;
    }

    public int getWeightNotStarted() {
        return weightNotStarted;
    }

    public int getWeightMemoryError() {
        return weightMemoryError;
    }

    public int getWeightNonViable() {
        return weightNonViable;
    }

    public int getWeightTimedOut() {
        return weightTimedOut;
    }

    public int getWeightSurvived() {
        return weightSurvived;
    }

    public int getWeightKilled() {
        return weightKilled;
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

    public int getWeightfailures() {
        return weightfailures;
    }

    public int getTotalTestsrun() {
        return totalTestsrun;
    }

    public int getWeightPassed() {
        return weightPassed;
    }
}
