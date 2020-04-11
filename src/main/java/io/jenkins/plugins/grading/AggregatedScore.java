package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Stores the scores of an autograding run. Persists the configuration and the scores for each metric.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
public class AggregatedScore {
    private AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
    private final List<AnalysisScore> analysisScores = new ArrayList<>();
    private int analysisAchieved;

    private TestConfiguration testsConfiguration = new TestConfiguration();
    private final List<TestScore> testScores = new ArrayList<>();
    private int testAchieved;

    private CoverageConfiguration coverageConfiguration = new CoverageConfiguration();
    private final List<CoverageScore> coverageScores = new ArrayList<>();
    private int coverageAchieved;

    private PitConfiguration pitConfiguration = new PitConfiguration();
    private final List<PitScore> pitScores = new ArrayList<>();
    private int pitAchieved;

    /**
     * Returns the number of achieved points.
     *
     * @return the number of achieved points
     */
    public int getAchieved() {
        return analysisAchieved + testAchieved + coverageAchieved + pitAchieved;
    }

    /**
     * Returns the total number of points that could be achieved.
     *
     * @return the total number of points that could be achieved
     */
    public int getTotal() {
        return analysisConfiguration.getMaxScore() + testsConfiguration.getMaxScore()
                + coverageConfiguration.getMaxScore() + pitConfiguration.getMaxScore();
    }

    public int getAnalysisAchieved() {
        return analysisAchieved;
    }

    public int getAnalysisRatio() {
        return getRatio(analysisConfiguration.getMaxScore(), getAnalysisAchieved());
    }

    public int getTestAchieved() {
        return testAchieved;
    }

    public int getTestRatio() {
        return getRatio(testsConfiguration.getMaxScore(), getTestAchieved());
    }

    public int getCoverageAchieved() {
        return coverageAchieved;
    }

    public int getCoverageRatio() {
        return getRatio(coverageConfiguration.getMaxScore(), getCoverageAchieved());
    }

    public int getPitAchieved() {
        return pitAchieved;
    }

    public int getPitRatio() {
        return getRatio(pitConfiguration.getMaxScore(), getPitAchieved());
    }

    /**
     * Returns the success ratio, i.e. number of achieved points divided by total points.
     *
     * @return the success ration
     */
    public int getRatio() {
        return getRatio(getTotal(), getAchieved());
    }

    private int getRatio(final int total, final int achieved) {
        if (total == 0) {
            return 100;
        }
        return achieved * 100 / total;
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

        int delta = aggregateDelta(scores);

        analysisAchieved = computeScore(analysisConfiguration.getMaxScore(), delta);
        return analysisAchieved;
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

        testAchieved = computeScore(configuration.getMaxScore(), score.getTotalImpact());
        return testAchieved;
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

        int delta = aggregateDelta(Arrays.asList(scores));

        coverageAchieved = computeScore(configuration.getMaxScore(), delta);
        return coverageAchieved;
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

        pitAchieved = computeScore(configuration.getMaxScore(), score.getTotalImpact());
        return pitAchieved;
    }

    private int computeScore(final int maxScore, final int totalImpact) {
        if (totalImpact <= 0) {
            return Math.max(0, maxScore + totalImpact);
        }
        else {
            return Math.min(maxScore, totalImpact);
        }
    }

    private int aggregateDelta(final List<? extends Score> scores) {
        int delta = 0;
        for (Score score : scores) {
            delta = delta + score.getTotalImpact();
        }
        return delta;
    }
}
