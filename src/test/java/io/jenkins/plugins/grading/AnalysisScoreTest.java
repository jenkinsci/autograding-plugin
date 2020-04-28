package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;

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
    private static final String NAME = "Results";
    private static final String ID = "result-id";

    @Test
    void shouldCalculate() {
        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(2);
        when(result.getTotalHighPrioritySize()).thenReturn(2);
        when(result.getTotalNormalPrioritySize()).thenReturn(2);
        when(result.getTotalLowPrioritySize()).thenReturn(2);
        when(result.getId()).thenReturn(ID);

        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setMaxScore(25)
                .setErrorImpact(-4)
                .setHighImpact(-3)
                .setNormalImpact(-2)
                .setLowImpact(-1)
                .build();
        AnalysisScore analysisScore = new AnalysisScore(NAME, analysisConfiguration, result);
        assertThat(analysisScore).hasTotalImpact(-8 - 6 - 4 - 2);
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
        when(result.getId()).thenReturn(ID);

        AnalysisScore analysisScore = new AnalysisScore(NAME, createConfigurationWithOnePointForEachSeverity(), result);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(3);
        assertThat(analysisScore).hasErrorsSize(3);
        assertThat(analysisScore).hasHighPrioritySize(5);
        assertThat(analysisScore).hasNormalPrioritySize(2);
        assertThat(analysisScore).hasLowPrioritySize(4);
        assertThat(analysisScore).hasName(NAME);
        assertThat(analysisScore).hasId(ID);
    }

    @Test
    void shouldReturnNegativeParams() {
        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(-3);
        when(result.getTotalHighPrioritySize()).thenReturn(-5);
        when(result.getTotalNormalPrioritySize()).thenReturn(-2);
        when(result.getTotalLowPrioritySize()).thenReturn(-4);
        when(result.getId()).thenReturn(ID);

        AnalysisScore analysisScore = new AnalysisScore(NAME, createConfigurationWithOnePointForEachSeverity(), result);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(-3);
        assertThat(analysisScore).hasErrorsSize(-3);
        assertThat(analysisScore).hasHighPrioritySize(-5);
        assertThat(analysisScore).hasNormalPrioritySize(-2);
        assertThat(analysisScore).hasLowPrioritySize(-4);
        assertThat(analysisScore).hasName(NAME);
        assertThat(analysisScore).hasId(ID);
    }

    private AnalysisConfiguration createConfigurationWithOnePointForEachSeverity() {
        return new AnalysisConfigurationBuilder()
                .setErrorImpact(1)
                .setHighImpact(1)
                .setNormalImpact(1)
                .setLowImpact(1)
                .build();
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        AnalysisResult result = mock(AnalysisResult.class);
        when(result.getTotalErrorsSize()).thenReturn(0);
        when(result.getTotalHighPrioritySize()).thenReturn(0);
        when(result.getTotalNormalPrioritySize()).thenReturn(0);
        when(result.getTotalLowPrioritySize()).thenReturn(0);

        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder()
                .setErrorImpact(0)
                .setHighImpact(0)
                .setNormalImpact(0)
                .setLowImpact(0)
                .build();

        when(result.getId()).thenReturn(ID);
        assertThatNullPointerException().isThrownBy(() -> new AnalysisScore(null, configuration, result));

        when(result.getId()).thenReturn(null);
        assertThatNullPointerException().isThrownBy(() -> new AnalysisScore(NAME, configuration, result));
    }
}
