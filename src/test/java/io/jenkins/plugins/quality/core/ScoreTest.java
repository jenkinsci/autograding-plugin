package io.jenkins.plugins.quality.core;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ScoreTest {

    @Test
    void addToScore() {
        Score score = new Score(100);
        score.addToScore(-5);
        Assertions.assertThat(score.getScore()).isEqualTo(95);
    }

}