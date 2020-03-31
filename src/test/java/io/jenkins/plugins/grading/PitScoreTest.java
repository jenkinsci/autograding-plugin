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
        PitConfiguration pitConfiguration = createConfiguration(25, -2, 1, 0);

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 5));

        assertThat(pits).hasTotalImpact(15);
    }

    @Test
    void shouldCalculateRatioImpacts() {
        PitConfiguration pitConfiguration = createConfiguration(20, 0, 0, -2);

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 3));

        assertThat(pits).hasTotalImpact(-20);
    }

    @Test
    void shouldCalculateNegativeResult() {
        PitConfiguration pitConfiguration = createConfiguration(25, -2, 1, 0);

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 20));

        assertThat(pits).hasTotalImpact(-30);
    }

    @Test
    void shouldCalculateZeroTotalImpact() {
        PitConfiguration pitConfiguration = createConfiguration(25, 0, 0, 0);

        PitScore pits = new PitScore(pitConfiguration, createAction(30, 20));

        assertThat(pits).hasTotalImpact(0);
    }

    @Test
    void shouldGetBuildActionId() {
        PitConfiguration pitConfiguration = createConfiguration(0, 0, 0, 0);
        PitBuildAction pitBuildAction = createAction(1, 0);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getId()).isEqualTo(pitBuildAction.getDisplayName());
    }

    @Test
    void shouldGetMutationSize() {
        PitConfiguration pitConfiguration = createConfiguration(0, 0, 0, 0);
        PitBuildAction pitBuildAction = createAction(1, 0);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getMutationsSize()).isEqualTo(1);
        assertThat(pits.getDetectedSize()).isEqualTo(1);
        assertThat(pits.getRatio()).isEqualTo(0);
    }

    @Test
    void shouldGetUndetectedSize() {
        PitConfiguration pitConfiguration = createConfiguration(0, 0, 0, 0);
        PitBuildAction pitBuildAction = createAction(1, 20);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getUndetectedSize()).isEqualTo(20);
    }

    @Test
    void shouldGetDetectedSize() {
        PitConfiguration pitConfiguration = createConfiguration(0, 0, 0, 0);
        PitBuildAction pitBuildAction = createAction(30, 20);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getDetectedSize()).isEqualTo(10);
    }

    @Test
    void shouldGetRatio() {
        PitConfiguration pitConfiguration = createConfiguration(0, 0, 0, 0);
        PitBuildAction pitBuildAction = createAction(100, 50);

        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getMutationsSize()).isEqualTo(100);
        assertThat(pits.getUndetectedSize()).isEqualTo(50);
        assertThat(pits.getDetectedSize()).isEqualTo(50);
        assertThat(pits.getRatio()).isEqualTo(50);
    }

    @Test
    void shouldCreateSimplePitScores() {
        PitConfiguration pitConfiguration = createConfiguration(45, -10, 10, 10);
        PitBuildAction pitBuildAction = createAction(80, 20);
        PitScore pits = new PitScore(pitConfiguration, pitBuildAction);

        assertThat(pits.getId()).isEqualTo(pitBuildAction.getDisplayName());
        assertThat(pits.getTotalImpact()).isEqualTo(650);
        assertThat(pits.getMutationsSize()).isEqualTo(80);
        assertThat(pits.getUndetectedSize()).isEqualTo(20);
        assertThat(pits.getDetectedSize()).isEqualTo(60);
        assertThat(pits.getRatio()).isEqualTo(25);

        PitConfiguration pitConfiguration2 = createConfiguration(10, 0, 5, 0);
        PitBuildAction pitBuildAction2 = createAction(50, 10);
        PitScore pits2 = new PitScore(pitConfiguration2, pitBuildAction2);

        assertThat(pits2.getId()).isEqualTo(pitBuildAction2.getDisplayName());
        assertThat(pits2.getTotalImpact()).isEqualTo(200);
        assertThat(pits2.getMutationsSize()).isEqualTo(50);
        assertThat(pits2.getUndetectedSize()).isEqualTo(10);
        assertThat(pits2.getDetectedSize()).isEqualTo(40);
        assertThat(pits2.getRatio()).isEqualTo(20);

        PitConfiguration pitConfiguration3 = createConfiguration(9000, 500, -500, 10);
        PitBuildAction pitBuildAction3 = createAction(80, 20);
        PitScore pits3 = new PitScore(pitConfiguration3, pitBuildAction3);

        assertThat(pits3.getId()).isEqualTo(pitBuildAction3.getDisplayName());
        assertThat(pits3.getTotalImpact()).isEqualTo(-19750);
        assertThat(pits3.getMutationsSize()).isEqualTo(80);
        assertThat(pits3.getUndetectedSize()).isEqualTo(20);
        assertThat(pits3.getDetectedSize()).isEqualTo(60);
        assertThat(pits3.getRatio()).isEqualTo(25);
    }

    private PitConfiguration createConfiguration(final int maxScore,
            final int undetectedImpact,
            final int detectedImpact,
            final int ratioImpact) {
        return new PitConfiguration.PitConfigurationBuilder().setMaxScore(maxScore)
                .setUndetectedImpact(undetectedImpact)
                .setDetectedImpact(detectedImpact)
                .setRatioImpact(ratioImpact)
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
        when(action.getDisplayName()).thenReturn("pit-build-action");
        return action;
    }
}
