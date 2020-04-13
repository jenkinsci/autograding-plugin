package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.sf.json.JSONObject;

import hudson.tasks.junit.TestResultAction;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link TestScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Lukas Kirner
 */
class TestScoreTest {
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @SuppressFBWarnings("UPM")
    private static Collection<Object[]> createTestConfigurationParameters() {
        return Arrays.asList(new Object[][] {
                {
                        createTestConfiguration(25, -1, -2, 1),
                        createAction(8, 1, 1),
                        3
                },
                {
                        createTestConfiguration(25, -1, -2, 1),
                        createAction(8, 5, 1),
                        -9
                },
                {
                        createTestConfiguration(25, -1, -2, -1),
                        createAction(8, 5, 1),
                        -13
                },
                {
                        createTestConfiguration(25, 0, 0, 0),
                        createAction(0, 0, 0),
                        0
                },
                {
                        createTestConfiguration(25, 99, 99, 99),
                        createAction(0, 0, 0),
                        0
                },
                {
                        createTestConfiguration(25, 1, 1, 1),
                        createAction(3, 3, 0),
                        3
                },
        });
    }

    @ParameterizedTest
    @MethodSource("createTestConfigurationParameters")
    void shouldComputeTestScoreWith(final TestConfiguration configuration,
            final TestResultAction resultAction, final int expectedTotalImpact) {
        TestScore test = new TestScore(configuration, resultAction);
        assertThat(test).hasTotalSize(resultAction.getTotalCount());
        assertThat(test).hasPassedSize(
                resultAction.getTotalCount() - resultAction.getFailCount() - resultAction.getSkipCount());
        assertThat(test).hasFailedSize(resultAction.getFailCount());
        assertThat(test).hasSkippedSize(resultAction.getSkipCount());
        assertThat(test).hasId(TestScore.ID);
        assertThat(test).hasName(resultAction.getDisplayName());
        assertThat(test).hasTotalImpact(expectedTotalImpact);
    }

    private static TestConfiguration createTestConfiguration(
            final int maxScore, final int skippedImpact, final int failureImpact, final int passedImpact) {
        return new TestConfiguration.TestConfigurationBuilder()
                .setMaxScore(maxScore)
                .setSkippedImpact(skippedImpact)
                .setFailureImpact(failureImpact)
                .setPassedImpact(passedImpact)
                .build();
    }

    private static TestResultAction createAction(final int totalSize, final int failedSize, final int skippedSize) {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getTotalCount()).thenReturn(totalSize);
        when(action.getFailCount()).thenReturn(failedSize);
        when(action.getSkipCount()).thenReturn(skippedSize);
        when(action.getDisplayName()).thenReturn("Tests");
        return action;
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
