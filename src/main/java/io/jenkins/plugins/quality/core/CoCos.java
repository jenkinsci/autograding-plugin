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
    private int totalLines;
    private int ratio;

    /**
     * Creates a new instance of {@link CoCos} for code coverage results.
     *
     * @param id           the name of the check
     * @param totalCovered the total number of covered code
     * @param totalLines   the total number of missed code
     * @param ratio        the ratio of covered code
     */
    public CoCos(final String id, final int totalCovered, final int totalLines, final int ratio) {
        super();
        this.id = id;
        this.totalCovered = totalCovered;
        this.totalLines = totalLines;
        this.ratio = ratio;
    }

    /**
     * Calculates and saves new {@link Score}.
     *
     * @param configs  all Configurations
     * @param base     All instances of BaseResults
     * @param score    Score Object
     * @param listener Console log
     * @return returns the delta that has been changed in score
     */
    public int calculate(Configuration configs, CoCos base, Score score, TaskListener listener) {
        int change = 0;
        if (configs.isCtoCheck()) {
            change = change + configs.getWeightMissed() * (base.getTotalLines() - base.getTotalCovered());
            change = change + configs.getWeightCovered() * base.getTotalCovered();

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

    public int getTotalLines() {
        return totalLines;
    }

    public int getRatio() { return ratio; }

}
