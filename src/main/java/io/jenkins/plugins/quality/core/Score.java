package io.jenkins.plugins.quality.core;


import hudson.model.Run;

import java.util.List;

/**
        * Stores the results of a scoring run.
        * Provides support for persisting the results of the build and loading.
        *
        * @author Eva-Maria Zeintl
        */
public class Score {
            private final int score;
            private List<Configuration> configs;
            private final int maxScore;
            private List<BaseResults> bases;
            //private final String referenceBuildID;

            public Score(int score, int maxScore, final List<Configuration> config, final List<BaseResults> base) {
                  super();
                this.score = score;
                this.maxScore = maxScore;
                this.configs.addAll(config);
                this.bases.addAll(base);
            }

            public Score (final Run<?, ?> owner, final int score, final List<Configuration> config, final int maxScore, final Score previousScore) {
                this.score = score;
                configs.addAll(config);
                this.maxScore = maxScore;
            }


    public int getScore() {
        return score;
    }

    public List<Configuration> getConfigs() {
        return configs;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public List<BaseResults> getBases() {
        return bases;
    }

}
