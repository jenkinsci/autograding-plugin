package io.jenkins.plugins.grading;


import hudson.model.TaskListener;
import static io.jenkins.plugins.grading.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

class PitScoreTest {

    @Test
    void shouldCalculate() {
        PitConfiguration pitConfiguration = new PitConfigurationBuilder().setMaxScore(25)
                .setWeightUndetected(-2)
                .setWeightDetected(1)
                .build();

        PitScore pits = new PitScore("pitmutation", 30, 5, 16);

        assertThat(pits.calculate(pitConfiguration, TaskListener.NULL)).isEqualTo(15);
    }


    @Test
    void shouldCalculateNegativeResult() {
        PitConfiguration pitConfiguration = new PitConfigurationBuilder().setMaxScore(25)
                .setWeightUndetected(-2)
                .setWeightDetected(1)
                .build();

        PitScore pits = new PitScore("pitmutation", 30, 20, 33);

        assertThat(pits.calculate(pitConfiguration, TaskListener.NULL)).isEqualTo(-30);

    }
}
