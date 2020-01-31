package io.jenkins.plugins.quality.core;

import io.jenkins.plugins.coverage.CoverageAction;

import java.util.List;
import java.util.Map;

public class CoCos {

    public void compute(Configuration configs, List<CoverageAction> actions, Map<String, BaseResults> base,
                        Score score) {
        for (CoverageAction action : actions) {
            //read configs from XML File

            //save base Results

            calculate(configs, action, score);
        }
    }

    public void calculate(Configuration configs, CoverageAction action, Score score) {
        int change = 0;

        score.addToScore(change);
    }

}
