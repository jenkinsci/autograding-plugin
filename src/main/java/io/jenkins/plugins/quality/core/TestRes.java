package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import hudson.tasks.junit.TestResultAction;
import java.util.List;
import java.util.Map;

/**
 * takes {@link Configuration} and the results of Junit tests.
 * Saves default check results into {@link BaseResults}.
 * Calculates and updates quality score
 *
 * @author Eva-Maria Zeintl
 */
public class TestRes {

    public void compute(Configuration configs, List<TestResultAction> actions, Map<String, BaseResults> base,
                        Score score,final TaskListener listener) {
        for (TestResultAction action : actions) {
            //save base Results
            base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(), action.getResult().getPassCount(),
                    action.getTotalCount(), action.getResult().getFailCount(), action.getResult().getSkipCount()));

            calculate(configs, action, score, listener, base);
        }
    }

    public void calculate(Configuration configs, TestResultAction action, Score score,
                         final TaskListener listener, Map<String, BaseResults> base) {
        int change = 0;
        if (configs.isJtoCheck()) {
            change = change + configs.getWeightPassed() * action.getResult().getPassCount();
            change = change + configs.getWeightfailures() * action.getResult().getFailCount();
            change = change + configs.getWeightSkipped() * action.getResult().getSkipCount();

            if(configs.getJkindOfGrading().equals("absolute")) {
                listener.getLogger().println("[CodeQuality] "+action.getDisplayName()+" changed scored by: "+change);
                base.get(action.getDisplayName()).setTotalChange(change);
                score.addToScore(change);
            } else if (configs.getJkindOfGrading().equals("relative")) {

            }
        }

    }

}
