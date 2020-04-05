package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

import hudson.model.TaskListener;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.grading.CoverageConfiguration.CoverageConfigurationBuilder;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link CoverageScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Johannes Hintermaier
 */
class CoverageScoreTests {
    @Test
    void shouldCalculate() {
        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(-2)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(-2);
    }

    @Test
    void shouldConvertFromJson() {
        CoverageConfiguration configuration = CoverageConfiguration.from(JSONObject.fromObject("{\"maxScore\": 4, \"coveredImpact\":5, \"missedImpact\":3}"));
        assertThat(configuration).hasMaxScore(4);
        assertThat(configuration).hasCoveredImpact(5);
        assertThat(configuration).hasMissedImpact(3);
    }

    @Test
    void shouldInitializeWithDefault(){
        CoverageConfiguration configurationEmpty = CoverageConfiguration.from(JSONObject.fromObject("{}"));
        assertThat(configurationEmpty).hasMaxScore(0);
        assertThat(configurationEmpty).hasCoveredImpact(0);
        assertThat(configurationEmpty).hasMissedImpact(0);

        CoverageConfiguration configurationOneValue = CoverageConfiguration.from(JSONObject.fromObject("{\"maxScore\": 4}"));
        assertThat(configurationOneValue).hasMaxScore(4);
        assertThat(configurationOneValue).hasCoveredImpact(0);
        assertThat(configurationOneValue).hasMissedImpact(0);
    }

    @Test
    void shouldNotReadAdditionalAttributes(){
        CoverageConfiguration configuration = CoverageConfiguration.from(JSONObject.fromObject("{\"maxScore\": 2, \"coveredImpact\":3, \"missedImpact\":4, \"notRead\":5}"));
        assertThat(configuration).hasMaxScore(2);
        assertThat(configuration).hasCoveredImpact(3);
        assertThat(configuration).hasMissedImpact(4);
    }

}
