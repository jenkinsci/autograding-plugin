package io.jenkins.plugins.quality.core;

import hudson.model.TaskListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QualityEvaluatorTest {

    @Test
    void perform() {
    }

    @Test
    void shouldUpdateCocoGrade(){
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", false, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<CoCos> cocoBases = new ArrayList<>();
        cocoBases.add(new CoCos("coverage", 99, 100, 99));
        Score score = new Score(configs.getMaxScore());

    }

    @Test
    void shouldSetMinCocoGrade(){

    }

    @Test
    void shouldSetMaxCocoGrade(){

    }
}