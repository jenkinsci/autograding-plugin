package io.jenkins.plugins.quality.core;

import hudson.tasks.junit.TestResultAction;
import java.util.List;
import java.util.Map;

public class TestRes {

    public void compute(Configuration configs, List<TestResultAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (TestResultAction action : actions) {
            //read configs from XML File

            //save base Results
            base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(), action.getResult().getPassCount(),
                    action.getTotalCount(), action.getResult().getFailCount(), action.getResult().getSkipCount()));

            calculate(configs, action, score);
        }
    }

    public void calculate(Configuration configs, TestResultAction action, Score score) {
        int change = 0;
        if (configs.isJtoCheck()) {
            change = change + configs.getWeightPassed() * action.getResult().getPassCount();
            change = change + configs.getWeightfailures() * action.getResult().getFailCount();
            change = change + configs.getWeightSkipped() * action.getResult().getSkipCount();

            if(configs.getJkindOfGrading().equals("absolute")) {
                score.addToScore(change);
            } else if (configs.getJkindOfGrading().equals("relative")) {

            }
        }

    }

}
