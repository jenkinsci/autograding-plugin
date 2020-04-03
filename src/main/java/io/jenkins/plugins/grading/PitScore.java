package io.jenkins.plugins.grading;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;

/**
 * Computes the {@link Score} impact of PIT mutation test results. These results are obtained by inspecting a
 * {@link PitBuildAction} instance of the PIT plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class PitScore {
    private final String id;

    private final int totalImpact;

    private final int mutationsSize;

    private final int detectedSize;
    private final int undetectedSize;

    private final int ratio;

    public PitScore(final PitConfiguration configuration, final PitBuildAction action) {
        id = action.getDisplayName();

        mutationsSize = action.getReport().getMutationStats().getTotalMutations();
        undetectedSize = action.getReport().getMutationStats().getUndetected();
        detectedSize = mutationsSize - undetectedSize;
        ratio = 100 - detectedSize * 100 / mutationsSize;

        totalImpact = computeImpact(configuration);
    }

    private int computeImpact(final PitConfiguration configs) {
        int change = 0;

        change = change + configs.getUndetectedImpact() * undetectedSize;
        change = change + configs.getDetectedImpact() * detectedSize;
        change = change + configs.getRatioImpact() * ratio;

        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    public int getMutationsSize() {
        return mutationsSize;
    }

    public int getUndetectedSize() {
        return undetectedSize;
    }

    public int getDetectedSize() {
        return detectedSize;
    }

    public int getRatio() {
        return ratio;
    }
}
