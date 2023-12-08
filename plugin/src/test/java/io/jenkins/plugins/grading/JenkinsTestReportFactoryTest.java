package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;
import hudson.tasks.junit.TestResultAction;

import static edu.hm.hafner.grading.assertions.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsTestReportFactory}.
 *
 * @author Ullrich Hafner
 */
class JenkinsTestReportFactoryTest {
    private static final String ID = "testName";
    private static final String NAME = "Coverage";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getFailCount()).thenReturn(1);
        when(action.getSkipCount()).thenReturn(2);
        when(action.getTotalCount()).thenReturn(5);
        when(action.getDisplayName()).thenReturn(NAME);
        when(action.getUrlName()).thenReturn(ID);

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsTestReportFactory testSupplier = new JenkinsTestReportFactory(run);

        var tool = new ToolConfiguration(ID, NAME, "unused", "unused", "unused");
        var log = new FilteredLog("Test");

        assertThat(testSupplier.create(tool, log)).isEqualTo(testSupplier.createTestReport(2, 1, 2));
    }
}
