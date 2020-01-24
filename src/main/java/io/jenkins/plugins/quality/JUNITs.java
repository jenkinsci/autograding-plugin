package io.jenkins.plugins.quality.core;

import hudson.tasks.junit.TestResultAction;
import io.jenkins.plugins.analysis.core.model.ResultAction;

import java.util.List;
import java.util.Map;

public class JUNITs {

    public void compute(Map<String, Configuration> configs, List<TestResultAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (TestResultAction action : actions) {
            //read configs from XML File

            //save base Results
            base.put(action.getDisplayName(), new BaseResults(action.getDisplayName(), action.getResult().getPassCount(),
                    action.getTotalCount(), action.getResult().getFailCount(), action.getResult().getSkipCount()));

            calculate(configs, action, score);
        }
    }

    public void calculate(Map<String, Configuration> configs, TestResultAction action, Score score) {
        int change = 0;
        if (configs.get(action.getDisplayName()).isToCheck()) {
            change = change + configs.get(action.getDisplayName()).getJweightPassed() *
                    action.getResult().getPassCount();
            change = change + configs.get(action.getDisplayName()).getJweightfailures() *
                    action.getResult().getFailCount();
            change = change + configs.get(action.getDisplayName()).getJweightSkipped() *
                    action.getResult().getSkipCount();

        }
        score.addToScore(change);
    }

}
