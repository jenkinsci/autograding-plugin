package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import io.jenkins.plugins.analysis.core.model.ResultAction;

import java.util.List;
import java.util.Map;

/**
 * takes {@link Configuration} and the results of default checks.
 * Saves default check results into {@link BaseResults}.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class DefaultChecks {

    public void compute(Configuration configs, List<ResultAction> actions, Map<String, BaseResults> base,
                        Score score, final TaskListener listener) {

        for (ResultAction action : actions) {
            //save base Results
            base.put(action.getId(), new BaseResults(action.getId(), action.getResult().getTotalErrorsSize(),
                    action.getResult().getTotalHighPrioritySize(), action.getResult().getTotalNormalPrioritySize(),
                    action.getResult().getTotalLowPrioritySize(), action.getResult().getTotalSize()));

            calculate(configs, action, score, listener, base);
        }
    }

    public void calculate(Configuration configs, ResultAction action, Score score, final TaskListener listener,
                          Map<String, BaseResults> base) {
        int change = 0;
        if (configs.isDtoCheck()) {
            change = change + configs.getWeightError() * action.getResult().getTotalErrorsSize();
            change = change + configs.getWeightHigh() * action.getResult().getTotalHighPrioritySize();
            change = change + configs.getWeightNormal() * action.getResult().getTotalNormalPrioritySize();
            change = change + configs.getWeightLow() * action.getResult().getTotalLowPrioritySize();

            if (configs.getDkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] " + action.getId() + " changed scored by: " + change);
                base.get(action.getId()).setTotalChange(change);
                score.addToScore(change);
            } else if (configs.getDkindOfGrading().equals("relative")) {

            }
        }
    }

}
