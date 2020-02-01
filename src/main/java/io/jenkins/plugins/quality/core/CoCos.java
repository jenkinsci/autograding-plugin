package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import io.jenkins.plugins.coverage.CoverageAction;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CoCos {

    public void compute(Configuration configs, List<CoverageAction> actions, Map<String, BaseResults> base,
                        Score score, @Nonnull final TaskListener listener) {
        for (CoverageAction action : actions) {

            //base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(),action.getResult());

            calculate(configs, action, score, listener, base);
        }
    }

    public void calculate(Configuration configs, CoverageAction action, Score score,
                          @Nonnull final TaskListener listener, Map<String, BaseResults> base) {
        int change = 0;
        if (configs.isCtoCheck()) {
            //change = change + configs.getWeightMissed() * action.getResult().getTotalErrorsSize();
            //change = change + configs.getWeightCovered() *  action.getResult().getTotalHighPrioritySize();

            if(configs.getDkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] "+action.getDisplayName()+" changed scored by: "+change);
                base.get(action.getDisplayName()).setTotalChange(change);
                score.addToScore(change);
            } else if (configs.getDkindOfGrading().equals("relative")) {

            }
        }
    }

}
