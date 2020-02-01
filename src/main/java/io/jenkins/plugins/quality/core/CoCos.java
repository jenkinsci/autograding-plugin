package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import io.jenkins.plugins.coverage.CoverageAction;
import java.util.List;
import java.util.Map;

/**
 * takes {@link Configuration} and the results of code coverage.
 * Saves default check results into {@link BaseResults}.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class CoCos {

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
    public void compute(Configuration configs, List<CoverageAction> actions, Map<String, BaseResults> base,
                        Score score, final TaskListener listener) {
        for (CoverageAction action : actions) {

            //base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(),action.getResult());

            calculate(configs, action, score, listener, base);
        }
    }

    /**
     * Calculates & saves new {@link Score}.
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
    public void calculate(Configuration configs, CoverageAction action, Score score,
                          final TaskListener listener, Map<String, BaseResults> base) {
        int change = 0;
        if (configs.isCtoCheck()) {
            //change = change + configs.getWeightMissed() * action.getResult().getTotalErrorsSize();
            //change = change + configs.getWeightCovered() *  action.getResult().getTotalHighPrioritySize();

            if (configs.getDkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] " + action.getDisplayName() + " changed score by: " + change);
                base.get(action.getDisplayName()).setTotalChange(change);
                score.addToScore(change);
            }
        }
    }

}
