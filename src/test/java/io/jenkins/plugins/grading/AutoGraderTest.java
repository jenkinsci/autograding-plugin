package io.jenkins.plugins.grading;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import static io.jenkins.plugins.grading.assertions.Assertions.*;
import org.junit.jupiter.api.Test;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.tasks.junit.TestResultAction;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link AutoGrader}.
 *
 * @author Ullrich Hafner
 */
class AutoGraderTest {

    @Test
    void shouldThrowExceptionOnBrokenConfiguration() {
        AutoGrader autoGrader = new AutoGrader("broken");

        Assertions.assertThatIllegalArgumentException().isThrownBy(() ->
                autoGrader.perform(mock(Run.class), new FilePath((VirtualChannel) null, "/"), mock(Launcher.class),
                        TaskListener.NULL));
    }

    @Test
    void shouldThrowNullPointerExceptionOnValidButNotCorrectConfiguration() {
        String json = "{ 'key': 'value' }";
        AutoGrader autoGrader = new AutoGrader(json);

        Assertions.assertThatNullPointerException().isThrownBy(() ->
                autoGrader.perform(mock(Run.class), new FilePath((VirtualChannel) null, "/"), mock(Launcher.class),
                        TaskListener.NULL));
    }

    @Test
    void shouldLogScoreFromRecordedTestResults() throws IOException {
        String testConfiguration = "{ 'tests' : "
                + "{ "
                + "'passedImpact' : 1, "
                + "'failureImpact' : 1, "
                + "'skippedImpact' : 1, "
                + "'maxScore' : 1 "
                + "} "
                + "}";
        String displayName = "testName";
        AutoGrader autoGrader = new AutoGrader(testConfiguration);

        TestResultAction action = mock(TestResultAction.class);
        when(action.getFailCount()).thenReturn(1);
        when(action.getSkipCount()).thenReturn(1);
        when(action.getTotalCount()).thenReturn(5);
        when(action.getDisplayName()).thenReturn(displayName);

        AggregatedScore score = mock(AggregatedScore.class);
        when(score.addTestsTotal(any(), any())).thenReturn(1);

        TestScore testScore = mock(TestScore.class);
        when(testScore.getTotalImpact()).thenReturn(1);

        Run run = mock(Run.class);
        when(run.getAction(any())).thenReturn(action);

        Path tempFile = Files.createTempFile("score", "xml");
        when(run.getRootDir()).thenReturn(tempFile.toFile());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out, true, StandardCharsets.UTF_8.name());
        TaskListener listener = mock(TaskListener.class);
        when(listener.getLogger()).thenReturn(printStream);
        autoGrader.perform(run, new FilePath((VirtualChannel) null, "/"), mock(Launcher.class), listener);

        assertThat(out.toString()).contains(String.format("[Autograding] Grading test results %s", displayName));
        assertThat(out.toString()).contains(String.format("[Autograding] -> Score %d - from recorded test results: %d, %d, %d, %d", 5, 5, 3, 1, 1));
        assertThat(out.toString()).contains(String.format("[Autograding] -> Score %d - from recorded test results: %d, %d, %d, %d", 5, 5, 3, 1, 1));
        assertThat(out.toString()).contains(String.format("[Autograding] Total score for test results: %s", 1));
    }
}
