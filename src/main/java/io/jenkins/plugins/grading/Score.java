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
public class Score {
    private int total;
    private int achieved;

    private AnalysisConfiguration analysisConfiguration;
    private final List<AnalysisScore> analysisScores = new ArrayList<>();
    private TestConfiguration testsConfiguration;
    private TestScore testsScore;
    private CoverageConfiguration coverageConfiguration;
    private CoverageScore coverageScore;
    private PitConfiguration pitConfiguration;
    private PitScore pitScore;

    public int getAchieved() {
        return achieved;
    }

    public int getTotal() {
        return total;
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
        return Collections.singletonList(testsScore);
    }

    public CoverageConfiguration getCoverageConfiguration() {
        return coverageConfiguration;
    }

    public List<CoverageScore> getCoverageScores() {
        return Collections.singletonList(coverageScore);
    }

    public PitConfiguration getPitConfiguration() {
        return pitConfiguration;
    }

    public List<PitScore> getPitScores() {
        return Collections.singletonList(pitScore);
    }

    public int addAnalysisTotal(final AnalysisConfiguration configuration, final List<AnalysisScore> scores) {
        analysisScores.addAll(scores);
        analysisConfiguration = configuration;

        int delta = 0;
        for (AnalysisScore score : scores) {
            delta = delta + score.getTotalImpact();
        }

        return updateScore(analysisConfiguration.getMaxScore(), delta);
    }

    public int addTestsTotal(final TestConfiguration configuration, final TestScore scores) {
        testsScore = scores;
        testsConfiguration = configuration;

        return updateScore(configuration.getMaxScore(), testsScore.getTotalImpact());
    }

    public int addCoverageTotal(final CoverageConfiguration configuration, final CoverageScore score) {
        this.coverageScore = score;
        this.coverageConfiguration = configuration;

        return updateScore(configuration.getMaxScore(), score.getTotalImpact());
    }

    public int addPitTotal(final PitConfiguration configuration, final PitScore score) {
        this.pitConfiguration = configuration;
        this.pitScore = score;

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
