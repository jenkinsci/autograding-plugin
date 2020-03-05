package io.jenkins.plugins.quality.core;


import hudson.model.TaskListener;
import static io.jenkins.plugins.quality.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

class PITsTest {

    @Test
    void shouldCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);

        PITs pits = new PITs("pitmutation", 30, 5, 16);

        assertThat(pits.calculate(configs, TaskListener.NULL)).isEqualTo(15);
    }


    @Test
    void shouldCalculateNegativeResult() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);

        PITs pits = new PITs("pitmutation", 30, 20, 33);

        assertThat(pits.calculate(configs, TaskListener.NULL)).isEqualTo(-30);

    }

    @Test
    void shouldNotCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);


        PITs pits = new PITs("pitmutation", 30, 5, 16);

        assertThat(pits.calculate(configs, TaskListener.NULL)).isEqualTo(0);
    }
}