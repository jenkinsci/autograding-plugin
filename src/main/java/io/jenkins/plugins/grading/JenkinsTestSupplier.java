package io.jenkins.plugins.grading;

import java.util.Collections;
import java.util.List;

import edu.hm.hafner.grading.TestConfiguration;
import edu.hm.hafner.grading.TestScore;
import edu.hm.hafner.grading.TestScore.TestScoreBuilder;
import edu.hm.hafner.grading.TestSupplier;

import hudson.model.Run;
import hudson.tasks.junit.TestResultAction;

/**
 * Supplies {@link TestScore test scores} based on the results of the registered
 * {@link TestResultAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsTestSupplier extends TestSupplier {
    private final Run<?, ?> run;

    JenkinsTestSupplier(final Run<?, ?> run) {
        this.run = run;
    }

    @Override
    protected List<TestScore> createScores(final TestConfiguration configuration) {
        TestResultAction action = run.getAction(TestResultAction.class);
        if (action != null) {
            TestScore score = new TestScoreBuilder().withConfiguration(configuration)
                    .withDisplayName(action.getDisplayName())
                    .withTotalSize(action.getTotalCount())
                    .withFailedSize(action.getFailCount())
                    .withSkippedSize(action.getSkipCount())
                    .build();
            return Collections.singletonList(score);
        }
        return Collections.emptyList();
    }
}
