package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hudson.tasks.junit.TestResultAction;

import static io.jenkins.plugins.grading.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link TestScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Lukas Kirner
 */

@RunWith(Parameterized.class)
public class TestScoreTest {

    @Parameter
    public TestConfiguration configuration;

    @Parameter(1)
    public TestResultAction resultAction;

    @Parameter(2)
    public int expect;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                createTestConfiguration(25,-1, -2, 1),
                createAction(8, 1, 1),
                3
            },
            {
                createTestConfiguration(25,-1, -2, 1),
                createAction(8, 5, 1),
                -9
            },
        });
    }

    @Test
    public void test() {
        TestScore test = new TestScore(configuration, resultAction);
        assertThat(test.getTotalImpact()).isEqualTo(expect);
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
        return action;
    }
}
