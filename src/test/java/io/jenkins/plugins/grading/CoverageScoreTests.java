package io.jenkins.plugins.grading;

import hudson.model.TaskListener;
import static io.jenkins.plugins.grading.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

class CoverageScoreTests {
    @Test
    void shouldCalculate() {
        CoverageConfiguration coverageConfiguration = new CoverageConfiguration.CoverageConfigurationBuilder().setWeightCovered(1)
                .setWeightMissed(-2)
                .build();
        CoverageScore coCos = new CoverageScore("coverage", 99, 100, 99);

        assertThat(coCos.calculate(coverageConfiguration, TaskListener.NULL)).isEqualTo(-2);
    }
}
