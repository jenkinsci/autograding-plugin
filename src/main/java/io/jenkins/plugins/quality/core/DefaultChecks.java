package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of default checks.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class DefaultChecks {

    private String id;
    private int totalChange;

    //Default
    private int totalErrors;
    private int totalHighs;
    private int totalNormals;
    private int totalLows;
    private int sum;



    /**
     * Creates a new instance of {@link DefaultChecks} for Default Checks.
     *
     * @param id           the name of the check
     * @param totalErrors  the total number of errors
     * @param totalHighs   the total number of High issues
     * @param totalNormals the total number of Normal issues
     * @param totalLows    the total number of Low issues
     * @param sum          the total number of all issues
     */
    public DefaultChecks(final String id, final  int totalErrors, final int totalHighs, final int totalNormals,
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
     * Calculates and saves new {@link Score}.
     * @param configs
     *          all Configurations
     * @param base
     *          base Results of the calculated check
     * @param score
     *          Score Object
     * @param listener
     *          Console log
     * @return returns the delta that has been changed in score
     */
    public int calculate(Configuration configs, DefaultChecks base, Score score, TaskListener listener) {
        int change = 0;
        if (configs.isDtoCheck()) {
            change = change + configs.getWeightError() * base.getTotalErrors();
            change = change + configs.getWeightHigh() * base.getTotalHighs();
            change = change + configs.getWeightNormal() * base.getTotalNormals();
            change = change + configs.getWeightLow() * base.getTotalLows();

            if (configs.getDkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] " + base.getId() + " changed score by: " + change);
                score.addToScore(change);
            }
        }
        return change;
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

    public int getSum() {
        return sum;
    }
}
