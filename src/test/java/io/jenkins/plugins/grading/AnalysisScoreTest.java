package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link AnalysisScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Andreas Stiglmeier
 */
class AnalysisScoreTest {
    @Test
    void shouldCalculate() {
        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(1);
        when(result.getTotalHighPrioritySize()).thenReturn(1);
        when(result.getTotalNormalPrioritySize()).thenReturn(1);
        when(result.getTotalLowPrioritySize()).thenReturn(1);

        AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration.AnalysisConfigurationBuilder()
                .setMaxScore(25)
                .setErrorImpact(-4)
                .setHighImpact(-3)
                .setNormalImpact(-2)
                .setWeightLow(-1)
                .build();
        AnalysisScore analysisScore = new AnalysisScore("Analysis Results", analysisConfiguration, result);
        assertThat(analysisScore).hasTotalImpact(-4 - 3 - 2 - 1);
    }

    @Test
    void shouldCalculateMultiplicative() {
        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(5);
        when(result.getTotalHighPrioritySize()).thenReturn(-3);
        when(result.getTotalNormalPrioritySize()).thenReturn(0);
        when(result.getTotalLowPrioritySize()).thenReturn(7);

        AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration.AnalysisConfigurationBuilder()
                .setMaxScore(25)
                .setErrorImpact(-4)
                .setHighImpact(-3)
                .setNormalImpact(-2)
                .setWeightLow(0)
                .build();
        AnalysisScore analysisScore = new AnalysisScore("Analysis Results", analysisConfiguration, result);
        assertThat(analysisScore).hasTotalImpact(-20 + 9);
    }

    @Test
    void shouldConvertFromJson() {
        AnalysisConfiguration configuration = AnalysisConfiguration.from(JSONObject.fromObject(
                "{\"maxScore\":5,\"errorImpact\":1,\"highImpact\":2,\"normalImpact\":3,\"lowImpact\":4}"));
        assertThat(configuration).hasErrorImpact(1);
        assertThat(configuration).hasHighImpact(2);
        assertThat(configuration).hasNormalImpact(3);
        assertThat(configuration).hasLowImpact(4);
        assertThat(configuration).hasMaxScore(5);
    }

    @Test
    void shouldReturnPositiveParams() {

        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(3);
        when(result.getTotalHighPrioritySize()).thenReturn(5);
        when(result.getTotalNormalPrioritySize()).thenReturn(2);
        when(result.getTotalLowPrioritySize()).thenReturn(4);
        when(result.getId()).thenReturn("result-id");

        AnalysisConfiguration configuration = mock(AnalysisConfiguration.class);
        when(configuration.getErrorImpact()).thenReturn(1);
        when(configuration.getHighImpact()).thenReturn(1);
        when(configuration.getLowImpact()).thenReturn(1);
        when(configuration.getNormalImpact()).thenReturn(1);

        AnalysisScore analysisScore = new AnalysisScore("Results", configuration, result);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(3);
        assertThat(analysisScore).hasErrorsSize(3);
        assertThat(analysisScore).hasHighPrioritySize(5);
        assertThat(analysisScore).hasNormalPrioritySize(2);
        assertThat(analysisScore).hasLowPrioritySize(4);
        assertThat(analysisScore).hasName("Results");
        assertThat(analysisScore).hasId("result-id");
    }

    @Test
    void shouldReturnNegativeParams() {

        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(-3);
        when(result.getTotalHighPrioritySize()).thenReturn(-5);
        when(result.getTotalNormalPrioritySize()).thenReturn(-2);
        when(result.getTotalLowPrioritySize()).thenReturn(-4);
        when(result.getId()).thenReturn("result-id");

        AnalysisConfiguration configuration = mock(AnalysisConfiguration.class);
        when(configuration.getErrorImpact()).thenReturn(1);
        when(configuration.getHighImpact()).thenReturn(1);
        when(configuration.getLowImpact()).thenReturn(1);
        when(configuration.getNormalImpact()).thenReturn(1);

        AnalysisScore analysisScore = new AnalysisScore("Results", configuration, result);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(-3);
        assertThat(analysisScore).hasErrorsSize(-3);
        assertThat(analysisScore).hasHighPrioritySize(-5);
        assertThat(analysisScore).hasNormalPrioritySize(-2);
        assertThat(analysisScore).hasLowPrioritySize(-4);
        assertThat(analysisScore).hasName("Results");
        assertThat(analysisScore).hasId("result-id");
    }

    @Test
    void shouldReturnNullParams() {

        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(0);
        when(result.getTotalHighPrioritySize()).thenReturn(0);
        when(result.getTotalNormalPrioritySize()).thenReturn(0);
        when(result.getTotalLowPrioritySize()).thenReturn(0);
        when(result.getId()).thenReturn(null);

        AnalysisConfiguration configuration = mock(AnalysisConfiguration.class);
        when(configuration.getErrorImpact()).thenReturn(0);
        when(configuration.getHighImpact()).thenReturn(0);
        when(configuration.getLowImpact()).thenReturn(0);
        when(configuration.getNormalImpact()).thenReturn(0);

        AnalysisScore analysisScore = new AnalysisScore(null, configuration, result);

        assertThat(analysisScore).hasName(null);
        assertThat(analysisScore).hasId(null);
    }

}
