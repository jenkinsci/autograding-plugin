package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TestResTest {

    @Test
    void shouldCalculate() {
       
        Configuration configs = new Configuration(25, "default", true, -4,
                -3,-2, -1, 25, "PIT", true, -2,
                1, 25, "COCO",true, 1,-2, 25, "JUNIT", true,
                -1, -2, 1);



    }


}