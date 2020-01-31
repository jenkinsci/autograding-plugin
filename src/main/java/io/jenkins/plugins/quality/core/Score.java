package io.jenkins.plugins.quality.core;


import java.util.Map;

/**
 * Stores the results of a scoring run.
 * Provides support for persisting the results of the build and loading.
 *
 * @author Eva-Maria Zeintl
 */
public class Score {

    private int score;
    private Score previousScore;
    private Configuration configs;
    private int maxScore;
    private Map<String, BaseResults> bases;

    public Score(int score) {
        super();
        this.score = score;
    }

    public Score() {
        super();
    }

    /*
    public Score(int score, int maxScore, final Configuration config, final Map<String, BaseResults> base) {
        super();
        this.score = score;
        this.maxScore = maxScore;
        this.configs = config;
        this.bases.putAll(base);
    }

    public Score(final Run<?, ?> owner, final int score, Configuration config, final int maxScore, final Score previousScore) {
        this.score = score;
        this.configs = config;
        this.maxScore = maxScore;
        this.previousScore = previousScore;
    }*/


    public int getScore() {
        return score;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void addToScore(int change) {
        this.score = score + change;
    }

    public Configuration getConfigs() {
        return configs;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Map<String, BaseResults> getBases() {
        return bases;
    }

    public void addConfigs(Configuration configs) {
        this.configs = configs;
    }

    public void addBases(Map<String, BaseResults> bases) {
        this.bases.putAll(bases);
    }
}
