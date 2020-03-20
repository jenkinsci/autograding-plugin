package io.jenkins.plugins.grading;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

class ScoreTest {

    @Test
    void addToScore() {
        Score score = new Score(100);
        score.addToScore(-5);
        assertThat(score.getScore()).isEqualTo(95);
    }

}
