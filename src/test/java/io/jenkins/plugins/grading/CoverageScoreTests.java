package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.grading.CoverageConfiguration.CoverageConfigurationBuilder;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link CoverageScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Andreas Riepl
 */
class CoverageScoreTests {
    @Test
    void givenCoverageScore_whenRatio99Percent_thenTotalImpactShouldEqualImpactValue() {
        int missedImpactValue = -2;

        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpactValue)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(missedImpactValue);
    }

    @Test
    void givenCoverageScore_whenRatio90Percent_thenTotalImpactShouldBeTenTimesHigher() {
        int missedImpactValue = 6;

        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpactValue)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(90, 100));

        assertThat(coverageScore).hasTotalImpact(missedImpactValue*10);
    }

    @Test
    void givenCoverageScore_whenRatio1Percent_thenTotalImpactShouldBe99TimesHigher() {
        int missedImpactValue = -869;

        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpactValue)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(1, 100));

        assertThat(coverageScore).hasTotalImpact(missedImpactValue*99);
    }
}
