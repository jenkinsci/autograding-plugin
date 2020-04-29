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
 * Tests the class {@link AggregatedScore}.
 *
 * @author Ullrich Hafner
 * @author Oliver Scholz
 */
class ScoreTest {
    @Test
    void shouldInitializeToZero() {
        AggregatedScore score = new AggregatedScore();
        assertThat(score).hasAchieved(0);
        assertThat(score).hasTotal(0);
        assertThat(score).hasRatio(100);
    }

    @Test
    void shouldSumAnalysisConfiguration() {
        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder().setMaxScore(20).build();

        AggregatedScore noActionScore = new AggregatedScore();
        noActionScore.addAnalysisTotal(configuration, Collections.emptyList());
        assertThat(noActionScore).hasAchieved(20);
        assertThat(noActionScore).hasTotal(20);
        assertThat(noActionScore).hasRatio(100);
        assertThat(noActionScore).hasAnalysisAchieved(20);
        assertThat(noActionScore).hasAnalysisRatio(100);

        AggregatedScore oneActionScore = new AggregatedScore();
        oneActionScore.addAnalysisTotal(configuration, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(oneActionScore).hasAchieved(10);
        assertThat(oneActionScore).hasTotal(20);
        assertThat(oneActionScore).hasRatio(50);
        assertThat(oneActionScore).hasAnalysisAchieved(10);
        assertThat(oneActionScore).hasAnalysisRatio(50);

        AggregatedScore twoActionsScore = new AggregatedScore();
        twoActionsScore.addAnalysisTotal(configuration, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(twoActionsScore).hasAchieved(5);
        assertThat(twoActionsScore).hasTotal(20);
        assertThat(twoActionsScore).hasRatio(25);
        assertThat(twoActionsScore).hasAnalysisAchieved(5);
        assertThat(twoActionsScore).hasAnalysisRatio(25);
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

        AggregatedScore score = new AggregatedScore();
        score.addCoverageTotal(coverageConfiguration,
                new CoverageScore("Line", coverageConfiguration, Ratio.create(50, 100)),
                new CoverageScore("Branch", coverageConfiguration, Ratio.create(60, 100)));

        assertThat(score).hasAchieved(10);
        assertThat(score).hasTotal(100);
        assertThat(score).hasRatio(10);
        assertThat(score).hasCoverageAchieved(10);
        assertThat(score).hasCoverageRatio(10);

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

        AggregatedScore score = new AggregatedScore();
        score.addTestsTotal(testConfiguration, testScore);

        assertThat(score).hasAchieved(97);
        assertThat(score).hasTotal(100);
        assertThat(score).hasRatio(97);
        assertThat(score).hasTestAchieved(97);
        assertThat(score).hasTestRatio(97);

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

        AggregatedScore score = new AggregatedScore();
        score.addPitTotal(pitConfiguration, pitScore);

        assertThat(score).hasAchieved(98);
        assertThat(score).hasTotal(100);
        assertThat(score).hasRatio(98);
        assertThat(score).hasPitAchieved(98);
        assertThat(score).hasPitRatio(98);

        assertThat(score.getPitConfiguration()).hasMaxScore(100);
        assertThat(score.getPitConfiguration()).hasDetectedImpact(2);
    }

    @Test
    void shouldUpdateWithAllConfigurations() {
        AggregatedScore score = new AggregatedScore();
        assertThat(score).hasAchieved(0);

        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder()
                .setMaxScore(100)
                .build();

        PitScore pitScore = mock(PitScore.class);
        when(pitScore.getTotalImpact()).thenReturn(-20);

        int pitAchieved = score.addPitTotal(pitConfiguration, pitScore);

        assertThat(pitAchieved).isEqualTo(80);
        assertThat(score).hasAchieved(80);
        assertThat(score).hasTotal(100);
        assertThat(score).hasRatio(80);
        assertThat(score.getPitConfiguration()).isSameAs(pitConfiguration);
        assertThat(score).hasPitScores(pitScore);

        TestConfiguration testConfiguration = new TestConfigurationBuilder()
                .setMaxScore(100)
                .build();

        TestScore testScore = mock(TestScore.class);
        when(testScore.getTotalImpact()).thenReturn(40);

        int testsAchieved = score.addTestsTotal(testConfiguration, testScore);

        assertThat(testsAchieved).isEqualTo(40);
        assertThat(score).hasAchieved(120);
        assertThat(score).hasTotal(200);
        assertThat(score).hasRatio(60);
        assertThat(score.getTestConfiguration()).isSameAs(testConfiguration);
        assertThat(score).hasTestScores(testScore);

        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMaxScore(100)
                .build();

        CoverageScore coverageScore = mock(CoverageScore.class);
        when(coverageScore.getTotalImpact()).thenReturn(-70);

        int coverageAchieved = score.addCoverageTotal(coverageConfiguration, coverageScore);

        assertThat(coverageAchieved).isEqualTo(30);
        assertThat(score).hasAchieved(150);
        assertThat(score).hasTotal(300);
        assertThat(score).hasRatio(50);
        assertThat(score.getCoverageConfiguration()).isSameAs(coverageConfiguration);
        assertThat(score).hasCoverageScores(coverageScore);

        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setMaxScore(100)
                .build();

        AnalysisScore analysisScore = createAnalysisScore(49);
        
        int analysisAchieved = score.addAnalysisTotal(analysisConfiguration, Collections.singletonList(analysisScore));

        assertThat(analysisAchieved).isEqualTo(49);
        assertThat(score).hasAchieved(199);
        assertThat(score).hasTotal(400);
        assertThat(score).hasRatio(49);
        assertThat(score.getAnalysisConfiguration()).isSameAs(analysisConfiguration);
        assertThat(score.getAnalysisScores()).containsExactly(analysisScore);
    }
}
