package io.jenkins.plugins.quality.core;

import io.jenkins.plugins.coverage.CoverageAction;

import java.util.List;
import java.util.Map;

public class CoCos {

    public void compute(Configuration configs, List<CoverageAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (CoverageAction action : actions) {
            /*
            base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(),action.getResult().getChildElements().);
            , new BaseResults(action.getId(), action.getResult().getTotalErrorsSize(),
                    action.getResult().getTotalHighPrioritySize(), action.getResult().getTotalNormalPrioritySize(),
                    action.getResult().getTotalLowPrioritySize(), action.getResult().getNewErrorSize()));

            */
            calculate(configs, action, score);
        }
    }

    public void calculate(Configuration configs, CoverageAction action, Score score) {
        int change = 0;

        score.addToScore(change);
    }

}
