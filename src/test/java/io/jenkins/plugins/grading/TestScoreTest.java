package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONObject;

import hudson.tasks.junit.TestResultAction;

import static io.jenkins.plugins.grading.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link TestScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 */
class TestScoreTest {
    @Test
    void shouldCalculate() {
        TestConfiguration testsConfiguration = new TestConfiguration.TestConfigurationBuilder().setMaxScore(25)
                .setSkippedImpact(-1)
                .setFailureImpact(-2)
                .setPassedImpact(1)
                .build();

        TestResultAction action = createAction(8, 1, 1);
        TestScore test = new TestScore(testsConfiguration, action);

        assertThat(test.getTotalImpact()).isEqualTo(3);
    }

    private TestResultAction createAction(final int totalSize, final int failedSize, final int skippedSize) {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getTotalCount()).thenReturn(totalSize);
        when(action.getFailCount()).thenReturn(failedSize);
        when(action.getSkipCount()).thenReturn(skippedSize);
        return action;
    }

    @Test
    void shouldCalculateNegativeResult() {
        TestConfiguration testsConfiguration = new TestConfiguration.TestConfigurationBuilder().setMaxScore(25)
                .setSkippedImpact(-1)
                .setFailureImpact(-2)
                .setPassedImpact(1)
                .build();

        TestScore test = new TestScore(testsConfiguration, createAction(8, 5, 1));

        assertThat(test.getTotalImpact()).isEqualTo(-9);
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
