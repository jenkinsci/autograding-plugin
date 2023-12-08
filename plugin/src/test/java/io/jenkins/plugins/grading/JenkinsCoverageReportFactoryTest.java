package io.jenkins.plugins.grading;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.coverage.Coverage.CoverageBuilder;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.coverage.ModuleNode;
import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.model.ElementFormatter;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

import static edu.hm.hafner.grading.assertions.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsCoverageReportFactory}.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageReportFactoryTest {
    private static final String ID = "coverage";
    private static final String NAME = "Coverage";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        var coverageBuilder = new CoverageBuilder();
        coverageBuilder.withCovered(5).withTotal(10);

        CoverageBuildAction action = mock(CoverageBuildAction.class);
        coverageBuilder.withMetric(Metric.LINE);
        when(action.getValueForMetric(Baseline.PROJECT, Metric.LINE)).thenReturn(Optional.of(coverageBuilder.build()));

        var node = new ModuleNode("empty");
        when(action.getResult()).thenReturn(node);

        coverageBuilder.withMetric(Metric.BRANCH);
        when(action.getValueForMetric(Baseline.PROJECT, Metric.BRANCH)).thenReturn(Optional.of(coverageBuilder.build()));

        when(action.getUrlName()).thenReturn(ID);
        when(action.getDisplayName()).thenReturn(NAME);
        when(action.getFormatter()).thenReturn(new ElementFormatter());
        when(action.getUrlName()).thenReturn("coverage");

        Run<?, ?> run = mock(Run.class);
        when(run.getActions(any())).thenReturn(List.of(action));

        JenkinsCoverageReportFactory analysisSupplier = new JenkinsCoverageReportFactory(run);

        var tool = new ToolConfiguration(ID, NAME, "unused", "unused", "unused");
        var log = new FilteredLog("Test");

        var result = analysisSupplier.create(tool, log);
        assertThat(result.getName()).isEqualTo("Coverage");
        assertThat(result.getChildren()).containsExactly(node);
    }
}
