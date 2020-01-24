package io.jenkins.plugins.quality.core;


import java.io.Serializable;


public class Configuration {

    private int weightNoCoverage;
    private int weightRunError;
    private int weightStarted;
    private int weightNotStarted;
    private int weightMemoryError;
    private int weightNonViable;
    private int weightTimedOut;
    private int weightSurvived;
    private int weightKilled;
    private int weightCovered;
    private int weightMissed;
    private int jweightSkipped;
    private int jweightfailures;
    private int jtotalTestsrun;
    private int jweightPassed;
    private String id;
    private boolean toCheck;
    private String kindOfGrading;
    private int weightError;
    private int weightHigh;
    private int weightNormal;
    private int weightLow;

    public Configuration() {
        super();
    }

    //config for static analysis
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
    }


    public String getID() {
        return id;
    }

    public boolean isToCheck() {
        return toCheck;
    }

    public String getKind() {
        return kindOfGrading;
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


    @Override
    public String toString() {
        return "ID: " + id + "" + " Will this Check be considered: " + toCheck + " How will be graded: " + kindOfGrading + " In Case of an Error: " + weightError +
                " In Case of a high Issue: " + weightHigh + " In Case of a Normal Issue: " + weightNormal + " In Case of a Low Issue " + weightLow;
    }

    public int getJweightSkipped() {
        return jweightSkipped;
    }

    public int getJweightfailures() {
        return jweightfailures;
    }

    public int getJtotalTestsrun() {
        return jtotalTestsrun;
    }

    public int getWeightCovered() {
        return weightCovered;
    }

    public int getWeightMissed() {
        return weightMissed;
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

    public int getJweightPassed() {
        return jweightPassed;
    }
}
