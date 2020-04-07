package io.jenkins.plugins.grading;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest(name = "{index} => MissedImpact: {0} - Ratio: {1} - MultiplicationFactor: {2}")
    @CsvSource({
            "-2, 99, 1",
            "-2, 1, 99",
            "-2, 10, 90",
            "2, 10, 90"})
    void totalValueShouldBe100MinusRatioTimesHigherThanMissedInputValue(
            final int missedImpactValue, final int ratioValue, final int multiplicationFactor) {
        CoverageConfiguration coverageConfiguration = new CoverageConfigurationBuilder()
                .setMissedImpact(missedImpactValue)
                .build();
        CoverageScore coverageScore = new CoverageScore(coverageConfiguration, Ratio.create(ratioValue, 100));

        assertThat(coverageScore).hasTotalImpact(missedImpactValue * multiplicationFactor);
    }

}
