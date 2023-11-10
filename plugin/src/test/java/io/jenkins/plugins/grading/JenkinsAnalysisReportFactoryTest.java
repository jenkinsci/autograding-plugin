package io.jenkins.plugins.grading;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.analysis.core.model.ResultAction;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisLabelProvider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsTestReportFactory}.
 *
 * @author Ullrich Hafner
 */
class JenkinsAnalysisReportFactoryTest {
    private static final String ID = "analysis";
    private static final String NAME = "Static Analysis";

    @Test
    void shouldLogScoreFromRecordedAnalysisResults() {
        var result = mock(AnalysisResult.class);
        when(result.getId()).thenReturn(ID);
        when(result.getTotalSize()).thenReturn(100);

        var report = new Report();
        when(result.getIssues()).thenReturn(report);

        var action = mock(ResultAction.class);
        when(action.getResult()).thenReturn(result);
        when(action.getLabelProvider()).thenReturn(new StaticAnalysisLabelProvider(ID, NAME));
        when(action.getDisplayName()).thenReturn(NAME);
        when(action.getUrlName()).thenReturn(ID);
        when(action.getId()).thenReturn(ID);

        Run<?, ?> run = mock(Run.class);
        when(run.getActions(any())).thenReturn(List.of(action));

        JenkinsAnalysisReportFactory analysisSupplier = new JenkinsAnalysisReportFactory(run);

        var tool = new ToolConfiguration(ID, NAME, "unused");
        var log = new FilteredLog("Test");

        assertThat(analysisSupplier.create(tool, log)).isSameAs(report);
    }
}
