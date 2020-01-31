package io.jenkins.plugins.quality.core;

import io.jenkins.plugins.analysis.core.model.ResultAction;

import java.util.List;
import java.util.Map;

public class DefaultChecks {
    String configPath;

    public void compute(Configuration configs, List<ResultAction> actions, Map<String, BaseResults> base,
                        Score score) {

        for (ResultAction action : actions) {
            //save base Results
            base.put(action.getId(), new BaseResults(action.getId(), action.getResult().getTotalErrorsSize(),
                    action.getResult().getTotalHighPrioritySize(), action.getResult().getTotalNormalPrioritySize(),
                    action.getResult().getTotalLowPrioritySize(), action.getResult().getNewErrorSize()));

            calculate(configs, action, score);
        }
    }

    public void calculate(Configuration configs, ResultAction action, Score score) {
        int change = 0;
        if (configs.isDtoCheck()) {
            change = change + configs.getWeightError() * action.getResult().getTotalErrorsSize();
            change = change + configs.getWeightHigh() *  action.getResult().getTotalHighPrioritySize();
            change = change + configs.getWeightNormal() * action.getResult().getTotalNormalPrioritySize();
            change = change + configs.getWeightLow() * action.getResult().getTotalLowPrioritySize();

            if(configs.getDkindOfGrading().equals("absolute")) {
                score.addToScore(change);
            } else if (configs.getDkindOfGrading().equals("relative")) {

            }
        }
    }

}
