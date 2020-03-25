package io.jenkins.plugins.grading;

import edu.umd.cs.findbugs.annotations.NonNull;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import hudson.model.TaskListener;

import io.jenkins.plugins.util.LogHandler;

/**
 * takes {@link Configuration} and the results of pitmutations. Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class PitScore {

    private final String id;
    private int totalChange;

    //PIT
    private final int totalMutations;
    private final int totalUndetected;
    private final int totalDetected;
    private final float ratio;

    /**
     * Creates a new instance of {@link PitScore} for pitmutation results.
     *
     * @param id
     *         the name of the check
     * @param totalMutations
     *         the total number of mutations
     * @param totalUndetected
     *         the total number of undetected mutations
     * @param ratio
     *         the percent value of undetected mutations
     */
    public PitScore(final String id, final int totalMutations, final int totalUndetected, final float ratio) {
        super();
        this.id = id;
        this.totalMutations = totalMutations;
        this.totalUndetected = totalUndetected;
        this.totalDetected = totalMutations - totalUndetected;
        this.ratio = 100 - ratio;
    }

    public PitScore(final PitConfiguration pitConfiguration, final PitBuildAction action,
            final LogHandler logHandler) {
        this(action.getDisplayName(), action.getReport().getMutationStats().getTotalMutations(),
                action.getReport().getMutationStats().getUndetected(),
                action.getReport().getMutationStats().getKillPercent());
        totalChange = calc(pitConfiguration);

        logHandler.log("-> Score %d - from recorded PIT mutation results: %d, %d, %d, %d",
                totalChange, totalMutations, totalUndetected, totalDetected, ratio);
    }

    /**
     * Calculates and saves new {@link Score}.
     *
     * @param configs
     *         all Configurations
     * @param listener
     *         Console log
     *
     * @return returns the delta that has been changed in score
     */
    public int calculate(final PitConfiguration configs, @NonNull final TaskListener listener) {
        int change = calc(configs);
        listener.getLogger().println("[CodeQuality] " + id + " changed score by: " + change);
        return change;
    }

    private int calc(final PitConfiguration configs) {
        int change = 0;
        change = change + configs.getWeightUndetected() * totalUndetected;
        change = change + configs.getWeightDetected() * totalDetected;

        totalChange = change;
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
