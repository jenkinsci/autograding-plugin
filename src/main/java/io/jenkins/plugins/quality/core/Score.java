package io.jenkins.plugins.quality.core;


import org.jaxen.pantry.Test;

import java.time.chrono.JapaneseChronology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private List<DefaultChecks> defaultBases = new ArrayList<>();
    private List<CoCos> cocoBases = new ArrayList<>();
    private List<PITs> pitBases = new ArrayList<>();
    private List<TestRes> junitBases = new ArrayList<>();

    /**
     * Creates a new instance of {@link Score}.
     * @param score
     *          sets initial score
     */
    public Score(int score) {
        super();
        this.score = score;
    }

    /**
     * Creates a new instance of {@link Score}.
     */
    public Score() {
        super();
    }

    public int getScore() {
        return score;
    }

    /**
     * increase score by change.
     * @param change
     */
    public void addToScore(int change) {
        this.score = score + change;
    }

    public Configuration getConfigs() {
        return configs;
    }

    public List<DefaultChecks> getDefaultBases() {
        return defaultBases;
    }
    public List<PITs> getPitBases() {
        return pitBases;
    }
    public List<TestRes> getJunitBases() {
        return junitBases;
    }
    public List<CoCos> getCocoBases() {
        return cocoBases;
    }



    /**
     * Save configurations.
     * @param inputConfig
     */
    public void addConfigs(Configuration inputConfig) {
        this.configs = inputConfig;
    }

    /**
     * Save Default results.
     * @param inputBase
     */
    public void addDefaultBase(DefaultChecks inputBase) {
        this.defaultBases.add(inputBase);
    }

    /**
     * Save PIT results.
     * @param inputBases
     */
    public void addPitBase(PITs inputBases) {
        this.pitBases.add(inputBases);
    }

    /**
     * Save Coco results.
     * @param inputBases
     */
    public void addCocoBase(CoCos inputBases) {
        this.cocoBases.add(inputBases);
    }

    /**
     * Save junit results.
     * @param inputBases
     */
    public void addJunitBase(TestRes inputBases) {
        this.junitBases.add(inputBases);
    }
}
