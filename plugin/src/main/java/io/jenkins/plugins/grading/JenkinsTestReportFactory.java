package io.jenkins.plugins.grading;

import edu.hm.hafner.grading.AggregatedScore.TestReportFactory;
import edu.hm.hafner.grading.AggregatedScore.TestResult;
import edu.hm.hafner.grading.TestScore;
import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;
import hudson.tasks.junit.TestResultAction;

/**
 * Supplies {@link TestScore test scores} based on the results of the registered
 * {@link TestResultAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsTestReportFactory implements TestReportFactory {
    private final Run<?, ?> run;

    JenkinsTestReportFactory(final Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public TestResult create(final ToolConfiguration tool, final FilteredLog log) {
        TestResultAction action = run.getAction(TestResultAction.class);
        if (action == null) {
            log.logError("Scoring of test results has been enabled, but no results have been found.");
            return new TestResult(0, 0, 0);
        }
        log.logInfo("-> Found result action for %s: %s", tool.getId(), action.getDisplayName());
        return new TestResult(action.getTotalCount() - action.getFailCount() - action.getSkipCount(),
                action.getFailCount(), action.getSkipCount());
    }
}
