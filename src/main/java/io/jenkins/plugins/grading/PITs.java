package io.jenkins.plugins.grading;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;

/**
 * takes {@link Configuration} and the results of pitmutations.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class PITs {

    private final String id;
    private int totalChange;

    //PIT
    private final int totalMutations;
    private final int totalUndetected;
    private final int totalDetected;
    private final float ratio;

    /**
     * Creates a new instance of {@link PITs} for pitmutation results.
     *
     * @param id              the name of the check
     * @param totalMutations  the total number of mutations
     * @param totalUndetected the total number of undetected mutations
     * @param ratio           the percent value of undetected mutations
     */
    public PITs(final String id, final int totalMutations, final int totalUndetected, final float ratio) {
        super();
        this.id = id;
        this.totalMutations = totalMutations;
        this.totalUndetected = totalUndetected;
        this.totalDetected = totalMutations - totalUndetected;
        this.ratio = 100 - ratio;
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
        if (configs.isPtoCheck()) {
            change = change + configs.getWeightUndetected() * totalUndetected;
            change = change + configs.getWeightDetected() * totalDetected;

            listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
            totalChange = change;
            return change;
        }
        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalChange() {
        return totalChange;
    }

    public int getTotalMutations() {
        return totalMutations;
    }

    public int getTotalUndetected() {
        return totalUndetected;
    }

    public int getTotalDetected() {
        return totalDetected;
    }

    public float getRatio() {
        return ratio;
    }
}
