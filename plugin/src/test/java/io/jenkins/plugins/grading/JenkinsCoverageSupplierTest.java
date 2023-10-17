package io.jenkins.plugins.grading;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.coverage.Coverage.CoverageBuilder;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.grading.CoverageConfiguration;
import edu.hm.hafner.grading.CoverageConfiguration.CoverageConfigurationBuilder;
import edu.hm.hafner.grading.CoverageScore;
import edu.hm.hafner.grading.CoverageScore.CoverageScoreBuilder;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.model.ElementFormatter;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link io.jenkins.plugins.grading.JenkinsCoverageSupplier}.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageSupplierTest {
    private static final String DISPLAY_NAME = "coverage";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        var coverageBuilder = new CoverageBuilder();
        coverageBuilder.setCovered(5).setTotal(10);
        CoverageBuildAction action = mock(CoverageBuildAction.class);
        coverageBuilder.setMetric(Metric.LINE);
        when(action.getValueForMetric(Baseline.PROJECT, Metric.LINE)).thenReturn(Optional.of(coverageBuilder.build()));
        coverageBuilder.setMetric(Metric.BRANCH);
        when(action.getValueForMetric(Baseline.PROJECT, Metric.BRANCH)).thenReturn(Optional.of(coverageBuilder.build()));
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(action.getFormatter()).thenReturn(new ElementFormatter());

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsCoverageSupplier coverageSupplier = new JenkinsCoverageSupplier(run);
        CoverageConfiguration configuration = new CoverageConfigurationBuilder().build();

        List<CoverageScore> scores = coverageSupplier.createScores(configuration);

        CoverageScoreBuilder builder = new CoverageScoreBuilder()
                .withConfiguration(configuration)
                .withCoveredPercentage(50);
        assertThat(scores).hasSize(2).contains(
                builder.withId("line").withDisplayName("Line")
                        .build(),
                builder.withId("branch").withDisplayName("Branch")
                        .build()
        );
    }
}
