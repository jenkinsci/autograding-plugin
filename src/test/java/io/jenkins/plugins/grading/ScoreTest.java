package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;
import io.jenkins.plugins.grading.CoverageConfiguration.CoverageConfigurationBuilder;
import io.jenkins.plugins.grading.TestConfiguration.TestConfigurationBuilder;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link Score}.
 *
 * @author Ullrich Hafner
 * @author Oliver Scholz
 */
class ScoreTest {
    @Test
    void shouldSumAnalysisConfiguration() {
        Score score = new Score();

        assertThat(score).hasAchieved(0);

        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder().setMaxScore(20).build();
        score.addAnalysisTotal(configuration, Collections.emptyList());
        assertThat(score).hasAchieved(20);
        assertThat(score).hasTotal(20);
        assertThat(score).hasRatio(100);
        assertThat(score).hasStyle(Score.EXCELLENT);

        score.addAnalysisTotal(configuration, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(score).hasAchieved(30);
        assertThat(score).hasTotal(40);
        assertThat(score).hasRatio(75);
        assertThat(score).hasStyle(Score.EXCELLENT);

        score.addAnalysisTotal(configuration, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(score).hasAchieved(35);
        assertThat(score).hasTotal(60);
        assertThat(score).hasRatio(35 * 100 / 60);
        assertThat(score).hasStyle(Score.GOOD);
    }

    private AnalysisScore createAnalysisScore(final int total) {
        AnalysisScore analysisScore = mock(AnalysisScore.class);

        when(analysisScore.getTotalImpact()).thenReturn(total);

        return analysisScore;
    }

    @Test
    void shouldUpdateCoverage() {
        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMaxScore(100)
                .setMissedImpact(-1)
                .build();

        Score score = new Score();
        score.addCoverageTotal(coverageConfiguration,
                new CoverageScore("Line", coverageConfiguration, Ratio.create(50, 100)),
                new CoverageScore("Branch", coverageConfiguration, Ratio.create(60, 100)));

        assertThat(score).hasAchieved(10);
        assertThat(score).hasTotal(100);

        assertThat(score.getCoverageConfiguration()).hasMaxScore(100);
        assertThat(score.getCoverageConfiguration()).hasMissedImpact(-1);
    }

    @Test
    void shouldUpdateTests() {
        TestConfiguration testConfiguration = new TestConfigurationBuilder()
                .setMaxScore(100)
                .setFailureImpact(-3)
                .build();

        TestScore testScore = mock(TestScore.class);
        when(testScore.getTotalImpact()).thenReturn(-3);

        Score score = new Score();
        score.addTestsTotal(testConfiguration, testScore);

        assertThat(score).hasAchieved(97);
        assertThat(score).hasTotal(100);

        assertThat(score.getTestConfiguration()).hasMaxScore(100);
        assertThat(score.getTestConfiguration()).hasFailureImpact(-3);
    }

    @Test
    void shouldUpdatePit() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder()
                .setMaxScore(100)
                .setDetectedImpact(2)
                .build();

        PitScore pitScore = mock(PitScore.class);
        when(pitScore.getTotalImpact()).thenReturn(-2);

        Score score = new Score();
        score.addPitTotal(pitConfiguration, pitScore);

        assertThat(score).hasAchieved(98);
        assertThat(score).hasTotal(100);

        assertThat(score.getPitConfiguration()).hasMaxScore(100);
        assertThat(score.getPitConfiguration()).hasDetectedImpact(2);
    }

    @Test
    void shouldUpdateWithAllConfigurations() {
        Score score = new Score();
        assertThat(score).hasAchieved(0);

        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder()
                .setMaxScore(100)
                .build();

        PitScore pitScore = mock(PitScore.class);
        when(pitScore.getTotalImpact()).thenReturn(-20);

        score.addPitTotal(pitConfiguration, pitScore);

        assertThat(score).hasAchieved(80);
        assertThat(score).hasTotal(100);
        assertThat(score).hasRatio(80);
        assertThat(score).hasStyle(Score.EXCELLENT);
        assertThat(score.getPitConfiguration()).isSameAs(pitConfiguration);
        assertThat(score).hasPitScores(pitScore);

        TestConfiguration testConfiguration = new TestConfigurationBuilder()
                .setMaxScore(100)
                .build();

        TestScore testScore = mock(TestScore.class);
        when(testScore.getTotalImpact()).thenReturn(40);

        score.addTestsTotal(testConfiguration, testScore);

        assertThat(score).hasAchieved(120);
        assertThat(score).hasTotal(200);
        assertThat(score).hasRatio(60);
        assertThat(score).hasStyle(Score.GOOD);
        assertThat(score.getTestConfiguration()).isSameAs(testConfiguration);
        assertThat(score).hasTestScores(testScore);

        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMaxScore(100)
                .build();

        CoverageScore coverageScore = mock(CoverageScore.class);
        when(coverageScore.getTotalImpact()).thenReturn(-70);

        score.addCoverageTotal(coverageConfiguration, coverageScore);

        assertThat(score).hasAchieved(150);
        assertThat(score).hasTotal(300);
        assertThat(score).hasRatio(50);
        assertThat(score).hasStyle(Score.GOOD);
        assertThat(score.getCoverageConfiguration()).isSameAs(coverageConfiguration);
        assertThat(score).hasCoverageScores(coverageScore);

        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setMaxScore(100)
                .build();

        AnalysisScore analysisScore = createAnalysisScore(49);
        score.addAnalysisTotal(analysisConfiguration, Collections.singletonList(analysisScore));

        assertThat(score).hasAchieved(199);
        assertThat(score).hasTotal(400);
        assertThat(score).hasRatio(49);
        assertThat(score).hasStyle(Score.FAILURE);
        assertThat(score.getAnalysisConfiguration()).isSameAs(analysisConfiguration);
        assertThat(score.getAnalysisScores()).containsExactly(analysisScore);
    }
}
