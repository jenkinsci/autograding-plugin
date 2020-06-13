package io.jenkins.plugins.grading;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.AnalysisConfiguration;
import edu.hm.hafner.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.grading.AnalysisScore.AnalysisScoreBuilder;

import hudson.model.Run;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.analysis.core.model.ResultAction;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisLabelProvider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsTestSupplier}.
 *
 * @author Ullrich Hafner
 */
class JenkinsAnalysisSupplierTest {
    private static final String DISPLAY_NAME = "testName";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        ResultAction action = mock(ResultAction.class);
        AnalysisResult result = mock(AnalysisResult.class);
        when(action.getResult()).thenReturn(result);
        when(action.getLabelProvider()).thenReturn(new StaticAnalysisLabelProvider(DISPLAY_NAME, DISPLAY_NAME));
        when(result.getId()).thenReturn(DISPLAY_NAME);
        when(result.getTotalErrorsSize()).thenReturn(1);
        when(result.getTotalHighPrioritySize()).thenReturn(2);
        when(result.getTotalNormalPrioritySize()).thenReturn(3);
        when(result.getTotalLowPrioritySize()).thenReturn(4);
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);

        Run<?, ?> run = mock(Run.class);
        when(run.getActions(any())).thenReturn(Collections.singletonList(action));

        JenkinsAnalysisSupplier analysisSupplier = new JenkinsAnalysisSupplier(run);
        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder().build();

        List<AnalysisScore> scores = analysisSupplier.createScores(configuration);

        assertThat(scores).hasSize(1).contains(new AnalysisScoreBuilder().withConfiguration(configuration)
                .withId(DISPLAY_NAME)
                .withDisplayName(DISPLAY_NAME)
                .withTotalErrorsSize(1)
                .withTotalHighSeveritySize(2)
                .withTotalNormalSeveritySize(3)
                .withTotalLowSeveritySize(4)
                .build());
    }
}
