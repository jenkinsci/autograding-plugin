package io.jenkins.plugins.quality.core;


import java.util.HashMap;
import java.util.Map;

/**
 * Stores the results of a scoring run.
 * Provides support for persisting the results of the build and loading.
 *
 * @author Eva-Maria Zeintl
 */
public class Score {

    private int score;
    private Configuration configs;
    private Map<String, BaseResults> bases = new HashMap<>();

    /**
     * Creates a new instance of {@link Score}.
     * @param score
     *          sets initial score
     */
    public Score(int score) {
        super();
        this.score = score;
    }

    public Score() {
        super();
    }

    public int getScore() {
        return score;
    }

    public void addToScore(int change) {
        this.score = score + change;
    }

    public Configuration getConfigs() {
        return configs;
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
