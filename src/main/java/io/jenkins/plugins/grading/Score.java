package io.jenkins.plugins.grading;


import java.util.ArrayList;
import java.util.List;

/**
 * Stores the results of a scoring run.
 * Provides support for persisting the results of the build and loading.
 *
 * @author Eva-Maria Zeintl
 */
public class Score {

    private int grade;
    private Configuration configs;
    private final List<DefaultChecks> defaultBases = new ArrayList<>();
    private final List<CoCos> cocoBases = new ArrayList<>();
    private final List<PITs> pitBases = new ArrayList<>();
    private final List<TestRes> junitBases = new ArrayList<>();

    /**
     * Creates a new instance of {@link Score}.
     * @param maxScore
     *          sets initial score
     */
    public Score(final int maxScore) {
        this.grade = maxScore;
    }

    /**
     * Creates a new instance of {@link Score}.
     */
    public Score() {
    }

    public int getScore() {
        return grade;
    }

    /**
     * increase score by change.
     * @param change calculated delta
     */
    public void addToScore(final int change) {
        this.grade = this.grade + change;
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
     * @param inputConfig configurations read from xml
     */
    public void addConfigs(final Configuration inputConfig) {
        this.configs = inputConfig;
    }

    /**
     * Save Default results.
     * @param inputBase results from static checks
     */
    public void addDefaultBase(final DefaultChecks inputBase) {
        this.defaultBases.add(inputBase);
    }

    /**
     * Save PIT results.
     * @param inputBases results from pit mutation check
     */
    public void addPitBase(final PITs inputBases) {
        this.pitBases.add(inputBases);
    }

    /**
     * Save Coco results.
     * @param inputBases results from code coverage check
     */
    public void addCocoBase(final CoCos inputBases) {
        this.cocoBases.add(inputBases);
    }

    /**
     * Save junit results.
     * @param inputBases results from junit tests
     */
    public void addJunitBase(final TestRes inputBases) {
        this.junitBases.add(inputBases);
    }
}
