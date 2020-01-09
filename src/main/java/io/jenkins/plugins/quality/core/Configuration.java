package io.jenkins.plugins.quality.core;


import java.io.Serializable;


public class Configuration {

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

    public void setId(String id) {
        this.id = id;
    }

    public void setToCheck(boolean toCheck) {
        this.toCheck = toCheck;
    }

    public void setKindOfGrading(String kindOfGrading) {
        this.kindOfGrading = kindOfGrading;
    }

    public void setWeightError(int weightError) {
        this.weightError = weightError;
    }

    public void setWeightHigh(int weightHigh) {
        this.weightHigh = weightHigh;
    }

    public void setWeightNormal(int weightNormal) {
        this.weightNormal = weightNormal;
    }

    public void setWeightLow(int weightLow) {
        this.weightLow = weightLow;
    }

    @Override
    public String toString() {
        return "ID: " + id + "" + " Will this Check be considered: " + toCheck + " How will be graded: " + kindOfGrading + " In Case of an Error: " + weightError +
                " In Case of a high Issue: " + weightHigh + " In Case of a Normal Issue: " + weightNormal + " In Case of a Low Issue " + weightLow;
    }
}
