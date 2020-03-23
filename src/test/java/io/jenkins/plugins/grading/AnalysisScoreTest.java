package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

import hudson.model.TaskListener;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link AnalysisScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
class AnalysisScoreTest {
    @Test
    void shouldCalculate() {
        AnalysisScore analysisScore = new AnalysisScore("default", 1, 1, 1, 1, 4);
        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder().setMaxScore(25)
                .setWeightError(-4)
                .setWeightHigh(-3)
                .setWeightNormal(-2)
                .setWeightLow(-1)
                .build();
        assertThat(analysisScore.calculate(analysisConfiguration, TaskListener.NULL)).isEqualTo(-4 - 3 - 2 - 1);
    }

    @Test
    void shouldConvertFromJson() {
        AnalysisConfiguration configuration = AnalysisConfiguration.from(JSONObject.fromObject(
                "{\"maxScore\":25,\"weightError\":0,\"weightHigh\":0,\"weightNormal\":0,\"weightLow\":0}"));
        assertThat(configuration.getMaxScore()).isEqualTo(25);
    }
}
