package io.jenkins.plugins.grading;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.TestConfiguration;
import edu.hm.hafner.grading.TestConfiguration.TestConfigurationBuilder;
import edu.hm.hafner.grading.TestScore;
import edu.hm.hafner.grading.TestScore.TestScoreBuilder;

import hudson.model.Run;
import hudson.tasks.junit.TestResultAction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsTestSupplier}.
 *
 * @author Ullrich Hafner
 */
class JenkinsTestSupplierTest {
    private static final String DISPLAY_NAME = "testName";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getFailCount()).thenReturn(1);
        when(action.getSkipCount()).thenReturn(2);
        when(action.getTotalCount()).thenReturn(5);
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsTestSupplier testSupplier = new JenkinsTestSupplier(run);
        TestConfiguration configuration = new TestConfigurationBuilder().build();

        List<TestScore> scores = testSupplier.createScores(configuration);

        assertThat(scores).hasSize(1).contains(new TestScoreBuilder().withConfiguration(configuration)
                .withDisplayName(DISPLAY_NAME)
                .withFailedSize(1)
                .withSkippedSize(2)
                .withTotalSize(5).build());
    }
}
