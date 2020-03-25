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
    private final List<AnalysisScore> analysisScores = new ArrayList<>();
    private final List<CoCos> cocoBases = new ArrayList<>();
    private final List<PITs> pitBases = new ArrayList<>();
    private final List<TestRes> junitBases = new ArrayList<>();
    private AnalysisConfiguration analysisConfiguration;
    private TestRes testScore;
    private TestsConfiguration testConfiguration;

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

    public List<AnalysisScore> getAnalysisScores() {
        return analysisScores;
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

    public AnalysisConfiguration getAnalysisConfiguration() {
        return analysisConfiguration;
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
    public void addAnalysisScore(final AnalysisScore inputBase) {
        this.analysisScores.add(inputBase);
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

    public int addAnalysisTotal(final AnalysisConfiguration configuration, final List<AnalysisScore> scores) {
        analysisScores.addAll(scores);
        analysisConfiguration = configuration;

        int delta = 0;
        for (AnalysisScore score : scores) {
            delta = delta + score.getTotalChange();
        }

        int actual;
        if (delta <= 0) {
            actual = Math.max(0, configuration.getMaxScore() + delta);
        }
        else {
            actual = Math.min(configuration.getMaxScore(), delta);
        }

        grade += actual;
        return actual;
    }

    public int addTestsTotal(final TestsConfiguration configuration, final TestRes scores) {
        testScore = scores;
        testConfiguration = configuration;

        int actual;
        if (testScore.getTotalChange() <= 0) {
            actual = Math.max(0, configuration.getMaxScore() + testScore.getTotalChange());
        }
        else {
            actual = Math.min(configuration.getMaxScore(), testScore.getTotalChange());
        }

        grade += actual;
        return actual;
    }
}
