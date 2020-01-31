package io.jenkins.plugins.quality.core;

public class BaseResults {
    private String id;
    //Default
    private int totalErrors = 0;
    private int totalHighs = 0;
    private int totalNormals = 0;
    private int totalLows = 0;
    //Junit
    private int totalPassed = 0;
    private int totalRun = 0;
    private int totalFailed = 0;
    private int totalSkipped = 0;
    //PIT
    private int totalNoCoverage;
    private int totalRunError;
    private int totalStarted;
    private int totalNotStarted;
    private int totalMemoryError;
    private int totalNonViable;
    private int totalTimedOut;
    private int totalSurvived;
    private int totalKilled;
    //CodeCoverage
    private int totalCovered;
    private int totalMissed;

    public BaseResults() {
        super();
    }

    //Default Constructor
    public BaseResults(String id, int totalErrors, int totalHighs, int totalNormals, int totalLows, int newError) {
        super();
        this.id = id;
        this.totalErrors = totalErrors;
        this.totalHighs = totalHighs;
        this.totalNormals = totalNormals;
        this.totalLows = totalLows;
    }

    //PIT Constructor
    public BaseResults(String id, int totalNoCoverage, int totalRunError, int totalStarted, int totalNotStarted,
                       int totalMemoryError, int totalNonViable, int totalTimedOut, int totalSurvived, int totalKilled) {
        super();
        this.id = id;
        this.totalNoCoverage = totalNoCoverage;
        this.totalRunError = totalRunError;
        this.totalStarted = totalStarted;
        this.totalNotStarted = totalNotStarted;
        this.totalMemoryError = totalMemoryError;
        this.totalNonViable = totalNonViable;
        this.totalTimedOut = totalSurvived;
        this.totalKilled = totalKilled;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public int getTotalHighs() {
        return totalHighs;
    }

    public void setTotalHighs(int totalHighs) {
        this.totalHighs = totalHighs;
    }

    public int getTotalNormals() {
        return totalNormals;
    }

    public void setTotalNormals(int totalNormals) {
        this.totalNormals = totalNormals;
    }

    public int getTotalLows() {
        return totalLows;
    }

    public void setTotalLows(int totalLows) {
        this.totalLows = totalLows;
    }

    public int getTotalPassed() {
        return totalPassed;
    }

    public void setTotalPassed(int totalPassed) {
        this.totalPassed = totalPassed;
    }

    public int getTotalRun() {
        return totalRun;
    }

    public void setTotalRun(int totalRun) {
        this.totalRun = totalRun;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public void setTotalSkipped(int totalSkipped) {
        this.totalSkipped = totalSkipped;
    }
}
