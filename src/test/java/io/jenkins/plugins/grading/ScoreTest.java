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

        score.addAnalysisTotal(configuration, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(score).hasAchieved(30);

        score.addAnalysisTotal(configuration, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(score).hasAchieved(35);
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
                .setMissedImpact(-2)
                .build();

        Score score = new Score();
        score.addCoverageTotal(coverageConfiguration,
                new CoverageScore(coverageConfiguration, Ratio.create(198, 200)));

        assertThat(score.getAchieved()).isEqualTo(98);
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

        assertThat(score.getAchieved()).isEqualTo(98);
    }
}
