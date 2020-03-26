package io.jenkins.plugins.grading;

import hudson.model.TaskListener;
import org.junit.jupiter.api.Test;

import static io.jenkins.plugins.grading.assertions.Assertions.*;


class TestsScoreTest {

    @Test
    void shouldCalculate() {
        TestConfiguration testsConfiguration = new TestConfiguration.TestConfigurationBuilder().setMaxScore(25)
                .setWeightSkipped(-1)
                .setWeightFailures(-2)
                .setWeightPassed(1)
                .build();
        TestScore test = new TestScore("Testergebnis", 6, 8, 1, 1);

        assertThat(test.calculate(testsConfiguration, TaskListener.NULL)).isEqualTo(3);
    }


    @Test
    void shouldCalculateNegativeResult() {

        TestConfiguration testsConfiguration = new TestConfiguration.TestConfigurationBuilder().setMaxScore(25)
                .setWeightSkipped(-1)
                .setWeightFailures(-2)
                .setWeightPassed(1)
                .build();

        TestScore test = new TestScore("Testergebnis", 2, 8, 5, 1);

        assertThat(test.calculate(testsConfiguration, TaskListener.NULL)).isEqualTo(-9);
    }
}
