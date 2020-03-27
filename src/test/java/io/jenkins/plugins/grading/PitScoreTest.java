package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutationStatsImpl;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link PitScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
class PitScoreTest {
    @Test
    void shouldCalculateSizeImpacts() {
        PitConfiguration pitConfiguration = createConfiguration();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 5));

        assertThat(pits).hasTotalImpact(15);
    }

    @Test
    void shouldCalculateRatioImpacts() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(20)
                .setRatioImpact(-2)
                .build();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 3));

        assertThat(pits).hasTotalImpact(-20);
    }

    @Test
    void shouldCalculateNegativeResult() {
        PitConfiguration pitConfiguration = createConfiguration();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 20));

        assertThat(pits.getTotalImpact()).isEqualTo(-30);
    }

    private PitConfiguration createConfiguration() {
        return new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setUndetectedImpact(-2)
                .setDetectedImpact(1)
                .build();
    }

    private PitBuildAction createAction(final int mutationsSize, final int undetectedSize) {
        PitBuildAction action = mock(PitBuildAction.class);
        ProjectMutations mutations = mock(ProjectMutations.class);
        MutationStatsImpl stats = mock(MutationStatsImpl.class);
        when(stats.getTotalMutations()).thenReturn(mutationsSize);
        when(stats.getUndetected()).thenReturn(undetectedSize);
        when(mutations.getMutationStats()).thenReturn(stats);
        when(action.getReport()).thenReturn(mutations);
        return action;
    }
}
