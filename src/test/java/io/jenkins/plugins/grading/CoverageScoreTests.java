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
        final CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-2, 0);
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(-2);
    }

    @Test
    void shouldCalculateTotalImpactWithZeroMissedImpact() {
        final CoverageConfiguration coverageConfiguration = createCoverageConfiguration(0, 5);
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(495);
    }

    @Test
    void shouldCalculateTotalImpact() {
        final CoverageConfiguration coverageConfiguration = createCoverageConfiguration(-1, 3);
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore).hasTotalImpact(296);
    }

    @Test
    void shouldGetId() {
        final CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder().build();
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        assertThat(coverageScore.getId()).isEqualTo("Line");
    }

    @Test
    void shouldGetTotalImpact() {
        final CoverageConfiguration coverageConfiguration = createCoverageConfiguration(1, 1);
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(99, 100));

        final int missedImpact = coverageConfiguration.getMissedImpact() * coverageScore.getMissedSize();
        final int coveredImpact = coverageConfiguration.getCoveredImpact() * coverageScore.getCoveredSize();
        assertThat(coverageScore.getTotalImpact()).isEqualTo(missedImpact + coveredImpact);
    }

    @Test
    void shouldGetCoveredSize() {
        final Ratio codeCoverageRatio = Ratio.create(99, 100);
        final CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder().build();
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, codeCoverageRatio);

        assertThat(coverageScore.getCoveredSize()).isEqualTo(codeCoverageRatio.getPercentage());
    }

    @Test
    void shouldGetMissedSize() {
        final Ratio codeCoverageRatio = Ratio.create(99, 100);
        final CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder().build();
        final CoverageScore coverageScore = new CoverageScore(coverageConfiguration, codeCoverageRatio);

        assertThat(coverageScore.getMissedSize()).isEqualTo(100 - codeCoverageRatio.getPercentage());
    }

    private CoverageConfiguration createCoverageConfiguration(final int missedImpact, final int coveredImpact) {
        return new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpact)
                .setCoveredImpact(coveredImpact)
                .build();
    }
}
