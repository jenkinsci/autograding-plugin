package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collections;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

/**
 * Tests the class {@link Score}.
 *
 * @author Ullrich Hafner
 */
class ScoreTest {

    @Test
    void addToScore() {
        Score score = new Score(100);
        score.addToScore(-5);
        assertThat(score.getScore()).isEqualTo(95);
    }

    @Test
    void shouldSumAnalysisConfiguration() {
        Score score = new Score();

        assertThat(score).hasScore(0);

        AnalysisConfiguration configuration = new AnalysisConfiguration.AnalysisConfigurationBuilder().setMaxScore(20).build();
        score.addAnalysisTotal(configuration, Collections.emptyList());
        assertThat(score).hasScore(20);

        score.addAnalysisTotal(configuration, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(score).hasScore(30);

        score.addAnalysisTotal(configuration, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(score).hasScore(35);
    }

    private AnalysisScore createAnalysisScore(final int total) {
        AnalysisScore analysisScore = mock(AnalysisScore.class);
        when(analysisScore.getTotalChange()).thenReturn(total);

        return analysisScore;
    }
}
