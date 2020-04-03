package io.jenkins.plugins.grading;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

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

}
