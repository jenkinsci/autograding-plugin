package edu.hm.hafner.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

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
 * @author Thomas Großbeck
 */
class PitScoreTest {
    @Test
    void shouldInitialiseConfigurationWithJson() {
        JSONObject json = new JSONObject();
        json.put("maxScore", 50);
        json.put("undetectedImpact", -2);
        json.put("detectedImpact", 1);
        json.put("ratioImpact", -1);

        PitConfiguration pitConfiguration = PitConfiguration.from(json);

        assertThat(pitConfiguration).hasMaxScore(50);
        assertThat(pitConfiguration).hasUndetectedImpact(-2);
        assertThat(pitConfiguration).hasDetectedImpact(1);
        assertThat(pitConfiguration).hasRatioImpact(-1);
    }

    @Test
    void shouldInitialiseConfigurationWithDefaultValues() {
        JSONObject json = new JSONObject();

        PitConfiguration pitConfiguration = PitConfiguration.from(json);

        assertThat(pitConfiguration).hasMaxScore(0);
        assertThat(pitConfiguration).hasUndetectedImpact(0);
        assertThat(pitConfiguration).hasDetectedImpact(0);
        assertThat(pitConfiguration).hasRatioImpact(0);
    }

    @Test
    void shouldInitialiseConfigurationWithJsonIgnoresAdditionalAttributes() {
        JSONObject json = new JSONObject();
        json.put("maxScore", 50);
        json.put("undetectedImpact", -2);
        json.put("detectedImpact", 1);
        json.put("ratioImpact", -1);
        json.put("additionalAttribute", 3);

        PitConfiguration pitConfiguration = PitConfiguration.from(json);

        assertThat(pitConfiguration).hasMaxScore(50);
        assertThat(pitConfiguration).hasUndetectedImpact(-2);
        assertThat(pitConfiguration).hasDetectedImpact(1);
        assertThat(pitConfiguration).hasRatioImpact(-1);
    }

    @Test
    void shouldCalculateSizeImpacts() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setUndetectedImpact(-2)
                .setDetectedImpact(1)
                .build();

        PitScore pits = new PitScore(pitConfiguration,
                createAction(30, 5).getReport().getMutationStats().getTotalMutations(),
                createAction(30, 5).getReport().getMutationStats().getUndetected(),
                createAction(30, 5).getDisplayName());

        assertThat(pits).hasTotalImpact(15);
    }

    @Test
    void shouldCalculateRatioImpacts() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setRatioImpact(-2)
                .build();

        PitScore pits = new PitScore(pitConfiguration,
                createAction(30, 3).getReport().getMutationStats().getTotalMutations(),
                createAction(30, 3).getReport().getMutationStats().getUndetected(),
                createAction(30, 3).getDisplayName());

        assertThat(pits).hasTotalImpact(-20);
    }

    @Test
    void shouldCalculateNegativeResult() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25)
                .setUndetectedImpact(-2)
                .setDetectedImpact(1)
                .build();

        PitScore pits = new PitScore(pitConfiguration,
                createAction(30, 20).getReport().getMutationStats().getTotalMutations(),
                createAction(30, 20).getReport().getMutationStats().getUndetected(),
                createAction(30, 20).getDisplayName());

        assertThat(pits).hasTotalImpact(-30);
    }

    @Test
    void shouldCalculateZeroTotalImpact() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(25).build();

        PitScore pits = new PitScore(pitConfiguration,
                createAction(30, 20).getReport().getMutationStats().getTotalMutations(),
                createAction(30, 20).getReport().getMutationStats().getUndetected(),
                createAction(30, 20).getDisplayName());

        assertThat(pits).hasTotalImpact(0);
    }

    @Test
    void shouldGetProperties() {
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder().setMaxScore(100)
                .setUndetectedImpact(-1)
                .setDetectedImpact(1)
                .build();
        PitBuildAction pitBuildAction = createAction(100, 25);

        PitScore pits = new PitScore(pitConfiguration,
                pitBuildAction.getReport().getMutationStats().getTotalMutations(),
                pitBuildAction.getReport().getMutationStats().getUndetected(), pitBuildAction.getDisplayName());

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
