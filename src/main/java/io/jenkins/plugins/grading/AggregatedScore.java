package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the scores of an autograding run. Persists the configuration and the scores for each metric.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
public class AggregatedScore {
    static final String EXCELLENT = "progress-bg-excellent";
    static final String GOOD = "progress-bg-good";
    static final String FAILURE = "progress-bg-failure";

    private static final int FAILURE_RATIO = 50;
    private static final int EXCELLENT_RATIO = 75;

    private int total;
    private int achieved;

    private AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
    private final List<AnalysisScore> analysisScores = new ArrayList<>();

    private TestConfiguration testsConfiguration = new TestConfiguration();
    private final List<TestScore> testScores = new ArrayList<>();

    private CoverageConfiguration coverageConfiguration = new CoverageConfiguration();
    private final List<CoverageScore> coverageScores = new ArrayList<>();

    private PitConfiguration pitConfiguration = new PitConfiguration();
    private final List<PitScore> pitScores = new ArrayList<>();

    /**
     * Returns the number of achieved points.
     *
     * @return the number of achieved points
     */
    public int getAchieved() {
        return achieved;
    }

    /**
     * Returns the total number of points that could be achieved.
     *
     * @return the total number of points that could be achieved
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns the success ratio, i.e. number of achieved points divided by total points.
     *
     * @return the success ration
     */
    public int getRatio() {
        return achieved * 100 / total;
    }

    /**
     * Returns a styling class that will be used to render the success progress bar.
     *
     * @return a styling class
     */
    public String getStyle() {
        if (getRatio() < FAILURE_RATIO) {
            return FAILURE;
        }
        else if (getRatio() < EXCELLENT_RATIO) {
            return GOOD;
        }
        return EXCELLENT;
    }

    public AnalysisConfiguration getAnalysisConfiguration() {
        return analysisConfiguration;
    }

    public List<AnalysisScore> getAnalysisScores() {
        return analysisScores;
    }

    public TestConfiguration getTestConfiguration() {
        return testsConfiguration;
    }

    public List<TestScore> getTestScores() {
        return testScores;
    }

    public CoverageConfiguration getCoverageConfiguration() {
        return coverageConfiguration;
    }

    public List<CoverageScore> getCoverageScores() {
        return coverageScores;
    }

    public PitConfiguration getPitConfiguration() {
        return pitConfiguration;
    }

    public List<PitScore> getPitScores() {
        return pitScores;
    }

    /**
     * Adds the specified collection of analysis grading scores.
     *
     * @param configuration
     *         the grading configuration
     * @param scores
     *         the scores to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addAnalysisTotal(final AnalysisConfiguration configuration, final List<AnalysisScore> scores) {
        analysisScores.addAll(scores);
        analysisConfiguration = configuration;

        int delta = 0;
        for (AnalysisScore score : scores) {
            delta = delta + score.getTotalImpact();
        }

        return updateScore(analysisConfiguration.getMaxScore(), delta);
    }

    /**
     * Adds a test grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param score
     *         the score to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addTestsTotal(final TestConfiguration configuration, final TestScore score) {
        testScores.add(score);
        testsConfiguration = configuration;

        return updateScore(configuration.getMaxScore(), score.getTotalImpact());
    }

    /**
     * Adds a coverage grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param scores
     *         the scores to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addCoverageTotal(final CoverageConfiguration configuration, final CoverageScore... scores) {
        Collections.addAll(coverageScores, scores);
        this.coverageConfiguration = configuration;

        int delta = aggregateDelta(scores);

        return updateScore(configuration.getMaxScore(), delta);
    }

    // TODO: create base class
    private int aggregateDelta(final CoverageScore[] scores) {
        int delta = 0;
        for (CoverageScore score : scores) {
            delta = delta + score.getTotalImpact();
        }
        return delta;
    }

    /**
     * Adds a PIT mutation testing grading score.
     *
     * @param configuration
     *         the grading configuration
     * @param score
     *         the score to take into account
     *
     * @return the total score impact (limited by the {@code maxScore} parameter of the configuration)
     */
    public int addPitTotal(final PitConfiguration configuration, final PitScore score) {
        this.pitConfiguration = configuration;
        this.pitScores.add(score);

        return updateScore(configuration.getMaxScore(), score.getTotalImpact());
    }

    private int updateScore(final int maxScore, final int totalChange) {
        total += maxScore;

        int actual;
        if (totalChange <= 0) {
            actual = Math.max(0, maxScore + totalChange);
        }
        else {
            actual = Math.min(maxScore, totalChange);
        }
        achieved += actual;

        return actual;
    }
}
