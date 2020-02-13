package io.jenkins.plugins.quality.core;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of default checks.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class DefaultChecks {

    private final String id;
    private int totalChange;

    //Default
    private final int totalErrors;
    private final int totalHighs;
    private final int totalNormals;
    private final int totalLows;
    private final int sum;



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
     * @param listener
     *          Console log
     * @return returns the delta that has been changed in score
     */
    public int calculate(final Configuration configs, @NonNull final TaskListener listener) {
        int change = 0;
        if (configs.isDtoCheck()) {
            change = change + configs.getWeightError() * totalErrors;
            change = change + configs.getWeightHigh() * totalHighs;
            change = change + configs.getWeightNormal() * totalNormals;
            change = change + configs.getWeightLow() * totalLows;

            listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
            setTotalChange(change);
            return change;
        }
        return change;
    }

    public void setTotalChange(final int totalChange) {
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
