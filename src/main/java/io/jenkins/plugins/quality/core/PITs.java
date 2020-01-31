package io.jenkins.plugins.quality.core;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import java.util.List;
import java.util.Map;

public class PITs {


    public void compute(Configuration configs, List<PitBuildAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (PitBuildAction action : actions) {
            //read configs from XML File

            //save base Results
            /*action.getReports().forEach();
            base.put(action.getDisplayName(), new BaseResults(action.getId(), action.total mutations, total uncovered, percent uncovered);

            */
            calculate(configs, action, score);
        }
    }

    public void calculate(Configuration configs, PitBuildAction action, Score score) {
        int change = 0;
        /*if (configs.get(action.getId()).isToCheck()) {
            change = change + configs.get(action.getId()).getWeightError() * action.getResult().getTotalErrorsSize();
            change = change + configs.get(action.getId()).getWeightHigh() * action.getResult().getTotalHighPrioritySize();
            change = change + configs.get(action.getId()).getWeightNormal() * action.getResult().getTotalNormalPrioritySize();
            change = change + configs.get(action.getId()).getWeightLow() * action.getResult().getTotalLowPrioritySize();

        }*/
        score.addToScore(change);
    }

}
