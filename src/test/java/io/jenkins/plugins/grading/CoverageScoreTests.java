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
 * @author Patrick Rogg
 */
class CoverageScoreTests {

    @Test
    void shouldCalculateTotalImpactWithZeroCoveredImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-2, 0);
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(-2);
    }

    @Test
    void shouldCalculateTotalImpactWithZeroMissedImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(0, 5);
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(495);
    }

    @Test
    void shouldCalculateTotalImpact() {
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-1, 3);
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(296);
    }

    @Test
    void shouldGetProperties() {
        Ratio codeCoverageRatio = Ratio.create(99, 100);
        CoverageConfiguration coverageConfiguration = createCoverageConfiguration(1, 1);
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, codeCoverageRatio);

        assertThat(coverageScore).hasId("Line");
        assertThat(coverageScore).hasCoveredSize(codeCoverageRatio.getPercentage());
        assertThat(coverageScore).hasMissedSize(100 - codeCoverageRatio.getPercentage());
    }

    private CoverageConfiguration createCoverageConfiguration(final int missedImpact, final int coveredImpact) {
        return new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpact)
                .setCoveredImpact(coveredImpact)
                .build();
    }
}
