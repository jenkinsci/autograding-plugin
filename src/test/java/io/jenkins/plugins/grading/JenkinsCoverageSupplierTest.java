package io.jenkins.plugins.grading;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.CoverageConfiguration;
import edu.hm.hafner.grading.CoverageConfiguration.CoverageConfigurationBuilder;
import edu.hm.hafner.grading.CoverageScore;
import edu.hm.hafner.grading.CoverageScore.CoverageScoreBuilder;

import hudson.model.Run;

import io.jenkins.plugins.coverage.CoverageAction;
import io.jenkins.plugins.coverage.targets.CoverageElement;
import io.jenkins.plugins.coverage.targets.CoverageResult;
import io.jenkins.plugins.coverage.targets.Ratio;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link JenkinsCoverageSupplier}.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageSupplierTest {
    private static final String DISPLAY_NAME = "coverage";

    @Test
    void shouldLogScoreFromRecordedTestResults() {
        CoverageAction action = mock(CoverageAction.class);
        CoverageResult result = mock(CoverageResult.class);
        when(result.getCoverage(CoverageElement.LINE)).thenReturn(Ratio.create(5, 10));
        when(result.getCoverage(CoverageElement.CONDITIONAL)).thenReturn(Ratio.create(5, 50));
        when(action.getResult()).thenReturn(result);
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsCoverageSupplier coverageSupplier = new JenkinsCoverageSupplier(run);
        CoverageConfiguration configuration = new CoverageConfigurationBuilder().build();

        List<CoverageScore> scores = coverageSupplier.createScores(configuration);

        CoverageScoreBuilder builder = new CoverageScoreBuilder().withConfiguration(configuration);
        assertThat(scores).hasSize(2).contains(
                builder.withId("line").withDisplayName("Line Coverage")
                        .withCoveredPercentage(50)
                        .build(),
                builder.withId("conditional").withDisplayName("Conditional Coverage")
                        .withCoveredPercentage(10)
                        .build()
        );

    }
}
