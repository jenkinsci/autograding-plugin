package edu.hm.hafner.grading;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.CoverageConfiguration.CoverageConfigurationBuilder;

import net.sf.json.JSONObject;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link CoverageScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Patrick Rogg
 * @author Johannes Hintermaier
 */
class CoverageScoreTests {
    private static final int PERCENTAGE = 99;

    @Test
    void shouldCalculateTotalImpactWithZeroCoveredImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-2, 0);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line",
                coverageConfiguration, PERCENTAGE);

        assertThat(coverageScore).hasTotalImpact(-2);
    }

    @Test
    void shouldCalculateTotalImpactWithZeroMissedImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(0, 5);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line",
                coverageConfiguration, PERCENTAGE
        );

        assertThat(coverageScore).hasTotalImpact(495);
    }

    @Test
    void shouldCalculateTotalImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-1, 3);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line",
                coverageConfiguration, PERCENTAGE);

        assertThat(coverageScore).hasTotalImpact(296);
    }

    @Test
    void shouldGetProperties() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(1, 1);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line",
                coverageConfiguration, PERCENTAGE);

        assertThat(coverageScore).hasName("Line");
        assertThat(coverageScore).hasCoveredPercentage(PERCENTAGE);
        assertThat(coverageScore).hasMissedPercentage(100 - PERCENTAGE);
    }

    private CoverageConfiguration createCoverageConfiguration(final int missedImpact, final int coveredImpact) {
        return new CoverageConfigurationBuilder()
                .setMissedPercentageImpact(missedImpact)
                .setCoveredPercentageImpact(coveredImpact)
                .build();
    }

    @Test
    void shouldConvertFromJson() {
        CoverageConfiguration configuration = CoverageConfiguration.from(
                JSONObject.fromObject(
                        "{\"maxScore\": 4, \"coveredPercentageImpact\":5, \"missedPercentageImpact\":3}"));
        assertThat(configuration).hasMaxScore(4);
        assertThat(configuration).hasCoveredPercentageImpact(5);
        assertThat(configuration).hasMissedPercentageImpact(3);
    }

    @Test
    void shouldInitializeWithDefault() {
        CoverageConfiguration configurationEmpty = CoverageConfiguration.from(JSONObject.fromObject("{}"));
        assertThat(configurationEmpty).hasMaxScore(0);
        assertThat(configurationEmpty).hasCoveredPercentageImpact(0);
        assertThat(configurationEmpty).hasMissedPercentageImpact(0);

        CoverageConfiguration configurationOneValue = CoverageConfiguration.from(
                JSONObject.fromObject("{\"maxScore\": 4}"));
        assertThat(configurationOneValue).hasMaxScore(4);
        assertThat(configurationOneValue).hasCoveredPercentageImpact(0);
        assertThat(configurationOneValue).hasMissedPercentageImpact(0);
    }

    @Test
    void shouldNotReadAdditionalAttributes() {
        CoverageConfiguration configuration = CoverageConfiguration.from(
                JSONObject.fromObject(
                        "{\"maxScore\": 2, \"coveredPercentageImpact\":3, \"missedPercentageImpact\":4, \"notRead\":5}"));
        assertThat(configuration).hasMaxScore(2);
        assertThat(configuration).hasCoveredPercentageImpact(3);
        assertThat(configuration).hasMissedPercentageImpact(4);
    }
}
