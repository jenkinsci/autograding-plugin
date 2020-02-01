package io.jenkins.plugins.quality.core;

/**
 * Saves all given Check Results in an Object for later use in GUI.
 *
 * @author Eva-Maria Zeintl
 */

public class BaseResults {

    private String id;
    private int totalChange;

    //Default
    private int totalErrors;
    private int totalHighs;
    private int totalNormals;
    private int totalLows;
    private int sum;

    //Junit
    private int totalPassed;
    private int totalRun;
    private int totalFailed;
    private int totalSkipped;

    //PIT
    private int totalMutations;
    private int totalUndetected;
    private float percentUndetected;

    //CodeCoverage
    private int totalCovered;
    private int totalMissed;


    /**
     * Creates a new instance of {@link BaseResults}.
     */
    public BaseResults() {
        super();
    }


    /**
     * Creates a new instance of {@link BaseResults} for Default Checks.
     *
     * @param id           the name of the check
     * @param totalErrors  the total number of errors
     * @param totalHighs   the total number of High issues
     * @param totalNormals the total number of Normal issues
     * @param totalLows    the total number of Low issues
     * @param sum          the total number of all issues
     */
    public BaseResults(final String id, final  int totalErrors, final int totalHighs, final int totalNormals,
                       final int totalLows, final int sum) {
        super();
        this.id = id;
        this.totalErrors = totalErrors;
        this.totalHighs = totalHighs;
        this.totalNormals = totalNormals;
        this.totalLows = totalLows;
        this.sum = sum;
    }


    /**
     * Creates a new instance of {@link BaseResults} for pitmutation results.
     *
     * @param id                the name of the check
     * @param totalMutations    the total number of mutations
     * @param totalUndetected   the total number of undetected mutations
     * @param percentUndetected the percent value of undetected mutations
     */
    public BaseResults(final String id, final int totalMutations, final int totalUndetected,
                       final float percentUndetected) {
        super();
        this.id = id;
        this.totalMutations = totalMutations;
        this.totalUndetected = totalUndetected;
        this.percentUndetected = percentUndetected;
    }


    /**
     * Creates a new instance of {@link BaseResults} for Junit results.
     *
     * @param id           the name of the check
     * @param totalPassed  the total number of passed tests
     * @param totalRun     the total number of run tests
     * @param totalFailed  the total number of failed tests
     * @param totalSkipped the total number of skipped tests
     */
    public BaseResults(final String id, final int totalPassed, final int totalRun, final int totalFailed,
                       final int totalSkipped) {
        super();
        this.id = id;
        this.totalPassed = totalPassed;
        this.totalFailed = totalFailed;
        this.totalRun = totalRun;
        this.totalSkipped = totalSkipped;
    }

    /**
     * Creates a new instance of {@link BaseResults} for code coverage results.
     *
     * @param id           the name of the check
     * @param totalCovered the total number of covered code
     * @param totalMissed  the total number of missed code
     */
    public BaseResults(final String id, final int totalCovered, final int totalMissed) {
        super();
        this.id = id;
        this.totalCovered = totalCovered;
        this.totalMissed = totalMissed;
    }

    /**
     * stores how much the issues for the id changes the score.
     *
     * @param totalChange
     */
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

    public float getPercentUndetected() {
        return percentUndetected;
    }

    public int getTotalCovered() {
        return totalCovered;
    }

    public int getTotalMissed() {
        return totalMissed;
    }
}
