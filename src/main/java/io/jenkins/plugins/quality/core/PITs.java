package io.jenkins.plugins.quality.core;

import io.jenkins.plugins.analysis.core.model.ResultAction;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import java.util.List;
import java.util.Map;

public class PITs {


    public void compute(Map<String, Configuration> configs, List<PitBuildAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (PitBuildAction action : actions) {
            //read configs from XML File

            //save base Results
            //base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(), );

            calculate(configs, action, score);
        }
    }

    public void calculate(Map<String, Configuration> configs, PitBuildAction action, Score score) {
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
