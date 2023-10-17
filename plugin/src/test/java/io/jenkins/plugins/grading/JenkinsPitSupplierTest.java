package io.jenkins.plugins.grading;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.coverage.Coverage.CoverageBuilder;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.grading.PitConfiguration;
import edu.hm.hafner.grading.PitConfiguration.PitConfigurationBuilder;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.PitScore.PitScoreBuilder;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsPitSupplier}.
 *
 * @author Ullrich Hafner
 */
class JenkinsPitSupplierTest {
    private static final String DISPLAY_NAME = "pit";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        var coverageBuilder = new CoverageBuilder();
        coverageBuilder.setMetric(Metric.MUTATION).setCovered(5).setTotal(10);
        CoverageBuildAction action = mock(CoverageBuildAction.class);
        when(action.getValueForMetric(Baseline.PROJECT, Metric.MUTATION)).thenReturn(Optional.of(coverageBuilder.build()));
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsPitSupplier pitSupplier = new JenkinsPitSupplier(run);
        PitConfiguration configuration = new PitConfigurationBuilder().build();

        List<PitScore> scores = pitSupplier.createScores(configuration);

        assertThat(scores).hasSize(1).contains(new PitScoreBuilder()
                .withConfiguration(configuration)
                        .withDisplayName(DISPLAY_NAME)
                        .withTotalMutations(10)
                        .withUndetectedMutations(5)
                        .build());
    }
}
