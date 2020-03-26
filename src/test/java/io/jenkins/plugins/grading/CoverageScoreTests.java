package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import hudson.model.TaskListener;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link CoverageScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
class CoverageScoreTests {
    @Test
    void shouldCalculate() {
        CoverageConfiguration coverageConfiguration = new CoverageConfiguration.CoverageConfigurationBuilder().setWeightCovered(1)
                .setWeightMissed(-2)
                .build();
        CoverageScore coverageScore = new CoverageScore("coverage", 99, 100, 99);

        assertThat(coverageScore.calculate(coverageConfiguration, TaskListener.NULL)).isEqualTo(-2);
    }
}
