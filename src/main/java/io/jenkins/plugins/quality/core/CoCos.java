package io.jenkins.plugins.quality.core;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of code coverage..
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class CoCos {

    private final String id;
    private int totalChange;

    //CodeCoverage
    private final int totalCovered;
    private final int totalLines;
    private final int ratio;
    private final int totalMissed;

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
        this.totalMissed = totalLines - totalCovered;
        this.ratio = ratio;
    }

    /**
     * Calculates and saves new {@link Score}.
     *
     * @param configs  all Configurations
     * @param listener Console log
     * @return returns the delta that has been changed in score
     */
    public int calculate(final Configuration configs, @NonNull final TaskListener listener) {
        int change = 0;
        if (configs.isCtoCheck()) {
            change = change + configs.getWeightMissed() * (100-ratio);

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

    public int getTotalCovered() {
        return totalCovered;
    }

    public int getTotalMissed() {
        return totalMissed;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getRatio() {
        return ratio;
    }

}
