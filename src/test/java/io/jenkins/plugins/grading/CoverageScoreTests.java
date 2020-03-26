package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import hudson.model.TaskListener;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.grading.CoverageConfiguration.CoverageConfigurationBuilder;

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
        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(-2)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore.getTotalImpact()).isEqualTo(-2);
    }
}
