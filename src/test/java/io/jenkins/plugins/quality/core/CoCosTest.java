package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoCosTest {

    @Test
    void shouldCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4,
                -3,-2, -1, 25, "PIT", true, -2,
                1, 25, "COCO",true, 1,-2, 25, "JUNIT", true,
                -1, -2, 1);

        CoCos coCos = new CoCos("coverage", 99, 100, 99);

        Assertions.assertThat(coCos.calculate(configs, TaskListener.NULL)).isEqualTo(-2);
    }

    @Test
    void shouldNotCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4,
                -3,-2, -1, 25, "PIT", false, -2,
                1, 25, "COCO",false, 1,-2, 25, "JUNIT", false,
                -1, -2, 1);

        CoCos coCos = new CoCos("coverage", 99, 100, 99);

        Assertions.assertThat(coCos.calculate(configs, TaskListener.NULL)).isEqualTo(0);
    }
}