package edu.hm.hafner.grading;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;

import net.sf.json.JSONObject;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link AnalysisScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Andreas Stiglmeier
 * @author Andreas Riepl
 * @author Oliver Scholz
 */
class AnalysisScoreTest {
    private static final String NAME = "Results";
    private static final String ID = "result-id";

    @Test
    void shouldCalculate() {
        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setErrorImpact(-4)
                .setHighImpact(-3)
                .setNormalImpact(-2)
                .setLowImpact(-1)
                .build();
        AnalysisScore analysisScore = new AnalysisScore(ID, NAME, analysisConfiguration,
                2, 2, 2, 2);
        assertThat(analysisScore).hasTotalImpact(2 * -4 - 2 * 3 - 2 * 2 - 2 * 1);
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
        AnalysisScore analysisScore = new AnalysisScore(ID, NAME, createConfigurationWithOnePointForEachSeverity(),
                3, 5, 2, 4);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(3);
        assertThat(analysisScore).hasErrorsSize(3);
        assertThat(analysisScore).hasHighSeveritySize(5);
        assertThat(analysisScore).hasNormalSeveritySize(2);
        assertThat(analysisScore).hasLowSeveritySize(4);
        assertThat(analysisScore).hasTotalSize(14);
        assertThat(analysisScore).hasName(NAME);
        assertThat(analysisScore).hasId(ID);
    }

    @Test
    void shouldReturnNegativeParams() {
        AnalysisScore analysisScore = new AnalysisScore(ID, NAME, createConfigurationWithOnePointForEachSeverity(),
                -3, -5, -2, -4);

        assertThat(analysisScore.getErrorsSize()).isEqualTo(-3);
        assertThat(analysisScore).hasErrorsSize(-3);
        assertThat(analysisScore).hasHighSeveritySize(-5);
        assertThat(analysisScore).hasNormalSeveritySize(-2);
        assertThat(analysisScore).hasLowSeveritySize(-4);
        assertThat(analysisScore).hasTotalSize(-14);
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
        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder()
                .setErrorImpact(0)
                .setHighImpact(0)
                .setNormalImpact(0)
                .setLowImpact(0)
                .build();

        assertThatNullPointerException().isThrownBy(() -> new AnalysisScore(ID, null, configuration,
                0, 0, 0, 0));

        assertThatNullPointerException().isThrownBy(() -> new AnalysisScore(null, NAME, configuration,
                0, 0, 0, 0));
    }

    @Test
    void shouldComputeImpactBySizeZero() {
        AnalysisConfiguration configuration = new AnalysisConfigurationBuilder()
                .setErrorImpact(100)
                .setHighImpact(100)
                .setNormalImpact(100)
                .setLowImpact(100)
                .build();

        AnalysisScore score = new AnalysisScore(ID, NAME, configuration,
                0, 0, 0, 0);
        assertThat(score).hasTotalImpact(0);
    }
}
