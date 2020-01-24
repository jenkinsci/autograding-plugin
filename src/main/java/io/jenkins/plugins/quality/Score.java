package io.jenkins.plugins.quality.core;


import hudson.model.Run;

import java.util.Map;
import java.util.Objects;
import java.io.Serializable;
import java.util.List;

/**
 * Stores the results of a scoring run.
 * Provides support for persisting the results of the build and loading.
 *
 * @author Eva-Maria Zeintl
 */
public class Score {

    private int score;
    private Score previousScore;
    private Map<String, Configuration> configs;
    private final int maxScore;
    private Map<String, BaseResults> bases;

    public Score() {
        super();
        this.score = 0;
        this.maxScore = 1000;
    }

    public Score(int score, int maxScore, final Map<String, Configuration> config, final Map<String, BaseResults> base) {
        super();
        this.score = score;
        this.maxScore = maxScore;
        this.configs.putAll(config);
        this.bases.putAll(base);
    }

    public Score(final Run<?, ?> owner, final int score, final Map<String, Configuration> config, final int maxScore, final Score previousScore) {
        this.score = score;
        configs.putAll(config);
        this.maxScore = maxScore;
        this.previousScore = previousScore;
    }


    public int getScore() {
        return score;
    }

    public void addToScore(int change) {
        this.score = score + change;
    }

    public Map<String, Configuration> getConfigs() {
        return configs;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Map<String, BaseResults> getBases() {
        return bases;
    }

    public void addConfigs(Map<String, Configuration> configs) {
        this.configs.putAll(configs);
    }

    public void addBases(Map<String, BaseResults> bases) {
        this.bases.putAll(bases);
    }
}
