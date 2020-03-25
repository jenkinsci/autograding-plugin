package io.jenkins.plugins.grading;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class QualityEvaluatorTest {

    @Test
    void shouldUpdateCocoGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<CoCos> cocoBases = new ArrayList<>();
        cocoBases.add(new CoCos("coverage", 99, 100, 99));
        Score score = new Score(configs.getMaxScore());
        score.addCocoBase(cocoBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateCocoGrade(configs, cocoBases, score);
        assertThat(score.getScore()).isEqualTo(98);
    }

    @Test
    void shouldSetMinCocoGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<CoCos> cocoBases = new ArrayList<>();
        cocoBases.add(new CoCos("coverage", 50, 100, 50));
        Score score = new Score(configs.getMaxScore());
        AutoGrader test = createAutoGrader();
        test.updateCocoGrade(configs, cocoBases, score);
        assertThat(score.getScore()).isEqualTo(75);
    }

    @Test
    void shouldSetMaxCocoGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<CoCos> cocoBases = new ArrayList<>();
        cocoBases.add(new CoCos("coverage", 101, 100, 101));
        Score score = new Score(configs.getMaxScore());
        AutoGrader test = createAutoGrader();
        test.updateCocoGrade(configs, cocoBases, score);
        assertThat(score.getScore()).isEqualTo(100);
    }

    @Test
    void shouldUpdateDefaultGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<AnalysisScore> defaultBases = new ArrayList<>();
        defaultBases.add(new AnalysisScore("default", 1, 0, 0, 2, 3));
        Score score = new Score(configs.getMaxScore());
        score.addAnalysisScore(defaultBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateAnalysisGrade(createAnalysisConfiguration(), score, defaultBases);
        assertThat(score.getScore()).isEqualTo(94);
    }

    private AnalysisConfiguration createAnalysisConfiguration() {
        return new AnalysisConfigurationBuilder().setMaxScore(25)
                .setWeightError(-4)
                .setWeightHigh(-3)
                .setWeightNormal(-2)
                .setWeightLow(-1)
                .build();
    }

    @Test
    void shouldSetMinDefaultGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<AnalysisScore> defaultBases = new ArrayList<>();
        defaultBases.add(new AnalysisScore("default", 10, 10, 0, 2, 20));
        Score score = new Score(configs.getMaxScore());
        score.addAnalysisScore(defaultBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateAnalysisGrade(createAnalysisConfiguration(), score, defaultBases);
        assertThat(score.getScore()).isEqualTo(75);
    }

    @Test
    void shouldSetMaxDefaultGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", false, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<AnalysisScore> defaultBases = new ArrayList<>();
        defaultBases.add(new AnalysisScore("default", -1, 0, 0, 0, -1));
        Score score = new Score(configs.getMaxScore());
        score.addAnalysisScore(defaultBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateAnalysisGrade(createAnalysisConfiguration(), score, defaultBases);
        assertThat(score.getScore()).isEqualTo(100);
    }

    @Test
    void shouldUpdatePITGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<PITs> pitBases = new ArrayList<>();
        pitBases.add(new PITs("pitmutation", 30, 12, 60));
        Score score = new Score(configs.getMaxScore());
        score.addPitBase(pitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updatePitGrade(configs, score, pitBases);
        assertThat(score.getScore()).isEqualTo(94);
    }

    @Test
    void shouldSetMinPITGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<PITs> pitBases = new ArrayList<>();
        pitBases.add(new PITs("pitmutation", 30, 25, 95));
        Score score = new Score(configs.getMaxScore());
        score.addPitBase(pitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updatePitGrade(configs, score, pitBases);
        assertThat(score.getScore()).isEqualTo(75);
    }

    @Test
    void shouldSetMaxPITGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", false, -1, -2, 1);
        List<PITs> pitBases = new ArrayList<>();
        pitBases.add(new PITs("pitmutation", 30, 5, 5));
        Score score = new Score(configs.getMaxScore());
        score.addPitBase(pitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updatePitGrade(configs, score, pitBases);
        assertThat(score.getScore()).isEqualTo(100);
    }

    @Test
    void shouldUpdateJUnitGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);
        List<TestRes> junitBases = new ArrayList<>();
        junitBases.add(new TestRes("Testergebnis", 0, 8, 7, 1));
        Score score = new Score(configs.getMaxScore());
        score.addJunitBase(junitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateJunitGrade(createTestsConfiguration(), score, junitBases);
        assertThat(score.getScore()).isEqualTo(85);
    }

    private TestsConfiguration createTestsConfiguration() {
        return new TestsConfigurationBuilder().setMaxScore(25)
                .setWeightSkipped(-1)
                .setWeightFailures(-2)
                .setWeightPassed(1)
                .build();
    }

    private AutoGrader createAutoGrader() {
        return new AutoGrader("{}");
    }

    @Test
    void shouldSetMinJUnitGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);
        List<TestRes> junitBases = new ArrayList<>();
        junitBases.add(new TestRes("Testergebnis", 0, 16, 16, 0));
        Score score = new Score(configs.getMaxScore());
        score.addJunitBase(junitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateJunitGrade(createTestsConfiguration(), score, junitBases);
        assertThat(score.getScore()).isEqualTo(75);
    }

    @Test
    void shouldSetMaxJUnitGrade() {
        Configuration configs = new Configuration(25, "default", true, -4, -3, -2, -1, 25,
                "PIT", true, -2, 1, 25, "COCO", true, 1, -2, 25, "JUNIT", true, -1, -2, 1);
        List<TestRes> junitBases = new ArrayList<>();
        junitBases.add(new TestRes("Testergebnis", 62, 62, 0, 0));
        Score score = new Score(configs.getMaxScore());
        score.addJunitBase(junitBases.get(0));
        AutoGrader test = createAutoGrader();
        test.updateJunitGrade(createTestsConfiguration(), score, junitBases);
        assertThat(score.getScore()).isEqualTo(100);
    }
}
