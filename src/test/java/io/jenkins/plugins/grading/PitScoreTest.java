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
 * @author Kevin Richter
 */
class PitScoreTest {
    @Test
    void shouldCalculateSizeImpacts() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setUndetectedImpact(-2)
                .setDetectedImpact(1)
                .build();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 5));

        assertThat(pits).hasTotalImpact(15);
    }

    @Test
    void shouldCalculateRatioImpacts() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setRatioImpact(-2)
                .build();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 3));

        assertThat(pits).hasTotalImpact(-20);
    }

    @Test
    void shouldCalculateNegativeResult() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setUndetectedImpact(-2)
                .setDetectedImpact(1)
                .build();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 20));

        assertThat(pits).hasTotalImpact(-30);
    }

    @Test
    void shouldCalculateZeroTotalImpact() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25).build();

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 20));

        assertThat(pits).hasTotalImpact(0);
    }

    @Test
    void shouldGetProperties() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(100)
                .setUndetectedImpact(-1)
                .setDetectedImpact(1)
                .build();
        PitBuildAction pitBuildAction = createAction(100, 25);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits).hasId(PitScore.ID);
        assertThat(pits).hasName(pitBuildAction.getDisplayName());
        assertThat(pits).hasTotalImpact(50);
        assertThat(pits).hasMutationsSize(100);
        assertThat(pits).hasDetectedSize(75);
        assertThat(pits).hasUndetectedSize(25);
        assertThat(pits).hasRatio(25);
    }

    private PitBuildAction createAction(final int mutationsSize, final int undetectedSize) {
        MutationStatsImpl stats = mock(MutationStatsImpl.class);
        when(stats.getTotalMutations()).thenReturn(mutationsSize);
        when(stats.getUndetected()).thenReturn(undetectedSize);

        ProjectMutations mutations = mock(ProjectMutations.class);
        when(mutations.getMutationStats()).thenReturn(stats);

        PitBuildAction action = mock(PitBuildAction.class);
        when(action.getReport()).thenReturn(mutations);
        when(action.getDisplayName()).thenReturn("pit-build-action");

        return action;
    }
}
