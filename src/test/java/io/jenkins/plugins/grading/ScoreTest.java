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

        score.addAnalysisTotal(20, Collections.emptyList());
        assertThat(score).hasScore(20);

        score.addAnalysisTotal(20, Collections.singletonList(createAnalysisScore(-10)));
        assertThat(score).hasScore(30);

        score.addAnalysisTotal(20, Arrays.asList(createAnalysisScore(-10), createAnalysisScore(-5)));
        assertThat(score).hasScore(35);
    }

    private AnalysisScore createAnalysisScore(final int total) {
        AnalysisScore analysisScore = mock(AnalysisScore.class);
        when(analysisScore.getTotalChange()).thenReturn(total);

        return analysisScore;
    }
}
