package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import static io.jenkins.plugins.quality.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

class DefaultChecksTest {

    @Test
    void shouldCalculate() {

        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);

        DefaultChecks defaultChecks = new DefaultChecks("default", 1, 1, 1, 1, 4);

        assertThat(defaultChecks.calculate(configs, TaskListener.NULL)).isEqualTo(-10);
    }


    @Test
    void shouldNotCalculate() {

        Configuration configs = new Configuration(25, "default", false, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);

        DefaultChecks defaultChecks = new DefaultChecks("default", 1, 1, 1, 1, 4);

        assertThat(defaultChecks.calculate(configs, TaskListener.NULL)).isEqualTo(0);
    }
}