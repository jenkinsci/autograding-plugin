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
        assertThat(analysisScore.getTotalImpact()).isEqualTo(-4 - 3 - 2 - 1);
    }

    @Test
    void shouldConvertFromJson() {
        AnalysisConfiguration configuration = AnalysisConfiguration.from(JSONObject.fromObject(
                "{\"maxScore\":25,\"weightError\":0,\"weightHigh\":0,\"weightNormal\":0,\"weightLow\":0}"));
        assertThat(configuration.getMaxScore()).isEqualTo(25);
    }
}
