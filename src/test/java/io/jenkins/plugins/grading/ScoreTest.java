package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;
import io.jenkins.plugins.grading.CoverageConfiguration.CoverageConfigurationBuilder;
import io.jenkins.plugins.util.LogHandler;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link Score}.
 *
 * @author Ullrich Hafner
 */
class ScoreTest {
    @Test
    void shouldSumAnalysisConfiguration() {
        Score score = new Score();

        assertThat(score).hasScore(0);

        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder().setMaxScore(20).build();
        score.addAnalysisTotal(configuration, Collections.emptyList());
        assertThat(score).hasScore(20);

        score.addAnalysisTotal(configuration, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(score).hasScore(30);

        score.addAnalysisTotal(configuration, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(score).hasScore(35);
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

        assertThat(score.getScore()).isEqualTo(98);
    }
}
