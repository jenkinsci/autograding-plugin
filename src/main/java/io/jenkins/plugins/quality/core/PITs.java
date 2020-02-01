package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import java.util.List;
import java.util.Map;

/**
 * takes {@link Configuration} and the results of pitmutations.
 * Saves default check results into {@link BaseResults}.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class PITs {

    /**
     * Saves {@link BaseResults}.
     * @param configs
     *          all Configurations
     * @param actions
     *          Input Action
     * @param base
     *          All instances of BaseResults
     * @param score
     *          Score Object
     * @param listener
     *          Console log
     */
    public void compute(final Configuration configs, final List<PitBuildAction> actions, Map<String, BaseResults> base,
                        Score score, final TaskListener listener) {
        for (PitBuildAction action : actions) {

            BaseResults put = base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(),
                    action.getReport().getMutationStats().getTotalMutations(),
                    action.getReport().getMutationStats().getUndetected(),
                    100 - action.getReport().getMutationStats().getKillPercent()));

            calculate(configs, action, score, base, listener);
        }
    }

    /**
     * Calculates and saves new {@link Score}.
     * @param configs
     *          all Configurations
     * @param action
     *          Input Action
     * @param base
     *          All instances of BaseResults
     * @param score
     *          Score Object
     * @param listener
     *          Console log
     */
    public void calculate(final Configuration configs, final PitBuildAction action, Score score,
                          Map<String, BaseResults> base, final TaskListener listener) {
        int change = 0;
        if (configs.isPtoCheck()) {
            change = change + configs.getWeightUndetected() * action.getReport().getMutationStats().getUndetected();

            if (configs.getDkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] " + action.getDisplayName() + " changed score by: " + change);
                base.get(action.getDisplayName()).setTotalChange(change);
                score.addToScore(change);
            }
        }
    }

}
