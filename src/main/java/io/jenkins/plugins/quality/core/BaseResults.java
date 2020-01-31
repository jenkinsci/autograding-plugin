package io.jenkins.plugins.quality.core;

public class BaseResults {
    private String id;
    private int totalChange = 0;
    //Default
    private int totalErrors = 0;
    private int totalHighs = 0;
    private int totalNormals = 0;
    private int totalLows = 0;
    private int sum = 0;
    //Junit
    private int totalPassed = 0;
    private int totalRun = 0;
    private int totalFailed = 0;
    private int totalSkipped = 0;
    //PIT
    private int totalMutations = 0;
    private int totalUndetected = 0;
    private int percentUndetected = 0;

    //CodeCoverage
    private int totalCovered = 0;
    private int totalMissed = 0;

    public BaseResults() {
        super();
    }

    //Default Constructor
    public BaseResults(String id, int totalErrors, int totalHighs, int totalNormals, int totalLows, int sum) {
        super();
        this.id = id;
        this.totalErrors = totalErrors;
        this.totalHighs = totalHighs;
        this.totalNormals = totalNormals;
        this.totalLows = totalLows;
        this.sum = sum;
    }

    //PIT Constructor
    public BaseResults(String id, int totalMutations, int totalUndetected, int percentUndetected) {
        super();
        this.id = id;
        this.totalMutations = totalMutations;
        this.totalUndetected = totalUndetected;
        this.percentUndetected = percentUndetected;
    }

    //Junit Constructor
    public BaseResults(String id, int totalPassed, int totalRun, int totalFailed, int totalSkipped) {
        super();
        this.id = id;
        this.totalPassed = totalPassed;
        this.totalFailed = totalFailed;
        this.totalRun = totalRun;
        this.totalSkipped = totalSkipped;
    }

    //code coverage Constructor
    public BaseResults(String id, int totalCovered, int totalMissed) {
        super();
        this.id = id;
        this.totalCovered = totalCovered;
        this.totalMissed = totalMissed;
    }

    public void setTotalChange(int totalChange) {
        this.totalChange = totalChange;
    }

    public String getId() {
        return id;
    }

    public int getTotalChange() {
        return totalChange;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public int getTotalHighs() {
        return totalHighs;
    }

    public int getTotalNormals() {
        return totalNormals;
    }

    public int getTotalLows() {
        return totalLows;
    }

    public int getTotalPassed() {
        return totalPassed;
    }

    public int getTotalRun() {
        return totalRun;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public int getSum() {
        return sum;
    }

    public int getTotalMutations() {
        return totalMutations;
    }

    public int getTotalUndetected() {
        return totalUndetected;
    }

    public int getPercentUndetected() {
        return percentUndetected;
    }

    public int getTotalCovered() {
        return totalCovered;
    }

    public int getTotalMissed() {
        return totalMissed;
    }
}
