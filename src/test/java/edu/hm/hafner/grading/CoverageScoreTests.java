package edu.hm.hafner.grading;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.CoverageConfiguration.CoverageConfigurationBuilder;

import net.sf.json.JSONObject;

import io.jenkins.plugins.coverage.targets.Ratio;

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
    @Test
    void shouldCalculateTotalImpactWithZeroCoveredImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-2, 0);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line", coverageConfiguration,
                Ratio.create(99, 100).getPercentage(), 100 - Ratio.create(99, 100).getPercentage()
        );

        assertThat(coverageScore).hasTotalImpact(-2);
    }

    @Test
    void shouldCalculateTotalImpactWithZeroMissedImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(0, 5);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line", coverageConfiguration,
                Ratio.create(99, 100).getPercentage(), 100 - Ratio.create(99, 100).getPercentage()
        );

        assertThat(coverageScore).hasTotalImpact(495);
    }

    @Test
    void shouldCalculateTotalImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-1, 3);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line", coverageConfiguration,
                Ratio.create(99, 100).getPercentage(), 100 - Ratio.create(99, 100).getPercentage()
        );

        assertThat(coverageScore).hasTotalImpact(296);
    }

    @Test
    void shouldGetProperties() {
        Ratio codeCoverageRatio = Ratio.create(99, 100);
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(1, 1);
        CoverageScore coverageScore = new CoverageScore(StringUtils.lowerCase("Line"), "Line", coverageConfiguration,
                codeCoverageRatio.getPercentage(), 100 - codeCoverageRatio.getPercentage()
        );

        assertThat(coverageScore).hasName("Line");
        assertThat(coverageScore).hasCoveredSize(codeCoverageRatio.getPercentage());
        assertThat(coverageScore).hasMissedSize(100 - codeCoverageRatio.getPercentage());
    }

    private CoverageConfiguration createCoverageConfiguration(final int missedImpact, final int coveredImpact) {
        return new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpact)
                .setCoveredImpact(coveredImpact)
                .build();
    }

    @Test
    void shouldConvertFromJson() {
        CoverageConfiguration configuration = CoverageConfiguration.from(
                JSONObject.fromObject("{\"maxScore\": 4, \"coveredImpact\":5, \"missedImpact\":3}"));
        assertThat(configuration).hasMaxScore(4);
        assertThat(configuration).hasCoveredImpact(5);
        assertThat(configuration).hasMissedImpact(3);
    }

    @Test
    void shouldInitializeWithDefault() {
        CoverageConfiguration configurationEmpty = CoverageConfiguration.from(JSONObject.fromObject("{}"));
        assertThat(configurationEmpty).hasMaxScore(0);
        assertThat(configurationEmpty).hasCoveredImpact(0);
        assertThat(configurationEmpty).hasMissedImpact(0);

        CoverageConfiguration configurationOneValue = CoverageConfiguration.from(
                JSONObject.fromObject("{\"maxScore\": 4}"));
        assertThat(configurationOneValue).hasMaxScore(4);
        assertThat(configurationOneValue).hasCoveredImpact(0);
        assertThat(configurationOneValue).hasMissedImpact(0);
    }

    @Test
    void shouldNotReadAdditionalAttributes() {
        CoverageConfiguration configuration = CoverageConfiguration.from(
                JSONObject.fromObject("{\"maxScore\": 2, \"coveredImpact\":3, \"missedImpact\":4, \"notRead\":5}"));
        assertThat(configuration).hasMaxScore(2);
        assertThat(configuration).hasCoveredImpact(3);
        assertThat(configuration).hasMissedImpact(4);
    }
}
