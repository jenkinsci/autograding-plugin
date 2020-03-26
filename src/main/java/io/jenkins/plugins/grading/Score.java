package io.jenkins.plugins.grading;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the results of a scoring run.
 * Provides support for persisting the results of the build and loading.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
public class Score {
    private int total;
    private int grade;

    private AnalysisConfiguration analysisConfiguration;
    private final List<AnalysisScore> analysisScores = new ArrayList<>();
    private TestConfiguration testsConfiguration;
    private TestScore testsScore;
    private CoverageConfiguration coverageConfiguration;
    private CoverageScore coverageScore;
    private PitConfiguration pitConfiguration;
    private PitScore pitScore;

    public int getScore() {
        return grade;
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
        total += analysisConfiguration.getMaxScore();

        int delta = 0;
        for (AnalysisScore score : scores) {
            delta = delta + score.getTotalImpact();
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

    public int addTestsTotal(final TestConfiguration configuration, final TestScore scores) {
        testsScore = scores;
        testsConfiguration = configuration;
        total += configuration.getMaxScore();

        int actual;
        if (testsScore.getTotalChange() <= 0) {
            actual = Math.max(0, configuration.getMaxScore() + testsScore.getTotalChange());
        }
        else {
            actual = Math.min(configuration.getMaxScore(), testsScore.getTotalChange());
        }

        grade += actual;
        return actual;
    }

    public int addCoverageTotal(final CoverageConfiguration configuration, final CoverageScore score) {
        this.coverageScore = score;
        this.coverageConfiguration = configuration;
        total += configuration.getMaxScore();

        int actual;
        if (score.getTotalChange() <= 0) {
            actual = Math.max(0, configuration.getMaxScore() + score.getTotalChange());
        }
        else {
            actual = Math.min(configuration.getMaxScore(), score.getTotalChange());
        }

        grade += actual;
        return actual;
    }

    public int addPitTotal(final PitConfiguration configuration, final PitScore score) {
        this.pitConfiguration = configuration;
        this.pitScore = score;

        int actual;
        if (score.getTotalChange() <= 0) {
            actual = Math.max(0, configuration.getMaxScore() + score.getTotalChange());
        }
        else {
            actual = Math.min(configuration.getMaxScore(), score.getTotalChange());
        }

        grade += actual;
        return actual;
    }
}
