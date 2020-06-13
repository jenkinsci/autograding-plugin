package io.jenkins.plugins.grading;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.PitConfiguration;
import edu.hm.hafner.grading.PitConfiguration.PitConfigurationBuilder;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.PitScore.PitScoreBuilder;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;
import hudson.model.Run;

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
        PitBuildAction action = mock(PitBuildAction.class);
        ProjectMutations mutations = mock(ProjectMutations.class);
        when(action.getReport()).thenReturn(mutations);
        MutationStats stats = mock(MutationStats.class);
        when(mutations.getMutationStats()).thenReturn(stats);
        when(stats.getTotalMutations()).thenReturn(10);
        when(stats.getUndetected()).thenReturn(3);
        when(action.getDisplayName()).thenReturn(DISPLAY_NAME);

        Run<?, ?> run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        JenkinsPitSupplier pitSupplier = new JenkinsPitSupplier(run);
        PitConfiguration configuration = new PitConfigurationBuilder().build();

        List<PitScore> scores = pitSupplier.createScores(configuration);

        assertThat(scores).hasSize(1).contains(new PitScoreBuilder().withConfiguration(configuration)
                        .withDisplayName(DISPLAY_NAME)
                        .withTotalMutations(10)
                        .withUndetectedMutations(3)
                        .build());
    }
}
