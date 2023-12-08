package io.jenkins.plugins.grading;

import edu.hm.hafner.coverage.ClassNode;
import edu.hm.hafner.coverage.ModuleNode;
import edu.hm.hafner.coverage.Node;
import edu.hm.hafner.coverage.TestCase.TestCaseBuilder;
import edu.hm.hafner.coverage.TestCase.TestResult;
import edu.hm.hafner.grading.AggregatedScore.TestReportFactory;
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
    public Node create(final ToolConfiguration tool, final FilteredLog log) {
        TestResultAction action = run.getAction(TestResultAction.class);
        if (action == null) {
            log.logError("Scoring of test results has been enabled, but no results have been found.");
            return createTestReport(0, 0, 0);
        }
        log.logInfo("-> Found result action for %s: %s", tool.getId(), action.getDisplayName());
        return createTestReport(action.getTotalCount() - action.getFailCount() - action.getSkipCount(),
                action.getFailCount(), action.getSkipCount());
    }

    Node createTestReport(final int passed, final int skipped, final int failed) {
        var root = new ModuleNode(String.format("Tests (%d/%d/%d)", failed, skipped, passed));
        var tests = new ClassNode("Tests");
        root.addChild(tests);

        for (int i = 0; i < failed; i++) {
            tests.addTestCase(new TestCaseBuilder()
                    .withTestName("test-failed-" + i)
                    .withClassName("test-class-failed-" + i)
                    .withMessage("failed-message-" + i)
                    .withDescription("StackTrace-" + i)
                    .withStatus(TestResult.FAILED).build());
        }
        for (int i = 0; i < skipped; i++) {
            tests.addTestCase(new TestCaseBuilder()
                    .withTestName("test-skipped-" + i)
                    .withStatus(TestResult.SKIPPED).build());
        }
        for (int i = 0; i < passed; i++) {
            tests.addTestCase(new TestCaseBuilder()
                    .withTestName("test-passed-" + i)
                    .withStatus(TestResult.PASSED).build());
        }

        return root;
    }
}
