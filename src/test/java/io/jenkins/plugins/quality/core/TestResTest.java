package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import org.junit.jupiter.api.Test;

import static io.jenkins.plugins.quality.assertions.Assertions.*;


class TestResTest {

    @Test
    void shouldCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);

        TestRes test = new TestRes("Testergebnis", 6, 8,
                1, 1);

        assertThat(test.calculate(configs, TaskListener.NULL)).isEqualTo(3);
    }


    @Test
    void shouldCalculateNegativeResult() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);

        TestRes test = new TestRes("Testergebnis", 2, 8,
                5, 1);

        assertThat(test.calculate(configs, TaskListener.NULL)).isEqualTo(-9);

    }

    @Test
    void shouldNotCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);

        TestRes test = new TestRes("Testergebnis", 6, 8,
                1, 1);

        assertThat(test.calculate(configs, TaskListener.NULL)).isEqualTo(0);
    }

}