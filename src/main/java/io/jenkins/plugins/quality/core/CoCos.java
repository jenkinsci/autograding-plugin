package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
/**
 * takes {@link Configuration} and the results of code coverage..
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class CoCos {

    private String id;
    private int totalChange;

    //CodeCoverage
    private int totalCovered;
    private int totalMissed;

    /**
     * Creates a new instance of {@link CoCos} for code coverage results.
     *
     * @param id           the name of the check
     * @param totalCovered the total number of covered code
     * @param totalMissed  the total number of missed code
     */
    public CoCos(final String id, final int totalCovered, final int totalMissed) {
        super();
        this.id = id;
        this.totalCovered = totalCovered;
        this.totalMissed = totalMissed;
    }

    /**
     * Calculates and saves new {@link Score}.
     * @param configs
     *          all Configurations
     * @param base
     *          All instances of BaseResults
     * @param score
     *          Score Object
     * @param listener
     *          Console log
     */
    public int calculate(Configuration configs, CoCos base, Score score, TaskListener listener) {
        int change = 0;
        if (configs.isCtoCheck()) {
            //change = change + configs.getWeightMissed() * action.getResult().getTotalErrorsSize();
            //change = change + configs.getWeightCovered() *  action.getResult().getTotalHighPrioritySize();

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

    public int getTotalCovered() {
        return totalCovered;
    }

    public int getTotalMissed() {
        return totalMissed;
    }

}
