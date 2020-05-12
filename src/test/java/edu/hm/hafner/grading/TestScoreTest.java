package edu.hm.hafner.grading;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.sf.json.JSONObject;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests the class {@link TestScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Lukas Kirner
 */
class TestScoreTest {

    private static final String NAME = "Tests";
    private static final int MAX_SCORE = 25;

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @SuppressFBWarnings("UPM")
    private static Collection<Object[]> createTestConfigurationParameters() {
        return Arrays.asList(new Object[][] {
                {
                        createTestConfiguration(-1, -2, 1),
                        8, 1, 1,
                        3
                },
                {
                        createTestConfiguration(-1, -2, 1),
                        8, 5, 1,
                        -9
                },
                {
                        createTestConfiguration(-1, -2, -1),
                        8, 5, 1,
                        -13
                },
                {
                        createTestConfiguration(0, 0, 0),
                        0, 0, 0,
                        0
                },
                {
                        createTestConfiguration(99, 99, 99),
                        0, 0, 0,
                        0
                },
                {
                        createTestConfiguration(1, 1, 1),
                        3, 3, 0,
                        3
                },
        });
    }

    @ParameterizedTest
    @MethodSource("createTestConfigurationParameters")
    void shouldComputeTestScoreWith(final TestConfiguration configuration,
            final int totalSize, final int failedSize, final int skippedSize, final int expectedTotalImpact) {
        TestScore test = new TestScore(NAME, configuration, totalSize, failedSize, skippedSize);

        assertThat(test).hasTotalSize(totalSize);
        assertThat(test).hasPassedSize(totalSize - failedSize - skippedSize);
        assertThat(test).hasFailedSize(failedSize);
        assertThat(test).hasSkippedSize(skippedSize);
        assertThat(test).hasId(TestScore.ID);
        assertThat(test).hasName(NAME);
        assertThat(test).hasTotalImpact(expectedTotalImpact);
    }

    private static TestConfiguration createTestConfiguration(
            final int skippedImpact, final int failureImpact, final int passedImpact) {
        return new TestConfiguration.TestConfigurationBuilder()
                .setMaxScore(MAX_SCORE)
                .setSkippedImpact(skippedImpact)
                .setFailureImpact(failureImpact)
                .setPassedImpact(passedImpact)
                .build();
    }

    @Test
    void shouldInitialiseWithDefaultValues() {
        TestConfiguration configuration = TestConfiguration.from(JSONObject.fromObject(
                "{}"));

        assertThat(configuration).hasMaxScore(0);
        assertThat(configuration).hasFailureImpact(0);
        assertThat(configuration).hasPassedImpact(0);
        assertThat(configuration).hasSkippedImpact(0);
    }

    /**
     * Tests the Fluent Interface Pattern for null return by setter functions.
     */
    @Test
    void shouldThrowNullPointerExceptionIfSetSkippedImpactReturnsNull() {
        TestConfiguration.TestConfigurationBuilder configurationBuilder = new TestConfiguration.TestConfigurationBuilder()
                .setSkippedImpact(0)
                .setPassedImpact(0);
        assertThat(configurationBuilder).isNotNull();
    }

    @Test
    void shouldIgnoresAdditionalAttributes() {
        TestConfiguration configuration = TestConfiguration.from(JSONObject.fromObject(
                "{\"additionalAttribute\":5}"));

        assertThat(configuration).hasMaxScore(0);
        assertThat(configuration).hasFailureImpact(0);
        assertThat(configuration).hasPassedImpact(0);
        assertThat(configuration).hasSkippedImpact(0);
    }

    @Test
    void shouldConvertFromJson() {
        TestConfiguration configuration = TestConfiguration.from(JSONObject.fromObject(
                "{\"maxScore\":5,\"failureImpact\":1,\"passedImpact\":2,\"skippedImpact\":3}"));

        assertThat(configuration).hasMaxScore(5);
        assertThat(configuration).hasFailureImpact(1);
        assertThat(configuration).hasPassedImpact(2);
        assertThat(configuration).hasSkippedImpact(3);
    }
}
