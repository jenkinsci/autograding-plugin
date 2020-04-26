package io.jenkins.plugins.grading;

import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.List;

import static io.jenkins.plugins.grading.assertions.Assertions.assertThat;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {

    private static final String AUTOGRADE_TESTS_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String AUTOGRADE_ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String AUTOGRADE_MUTATION_CONFIGURATION = "{\"pit\":{\"maxScore\":100,\"ratioImpact\":-1,\"detectedImpact\":0,\"undetectedImpact\":-1}}";
    private static final String COVERAGE_CONFIGURATION = "{\"coverage\": {\"maxScore\": 100,\"coveredImpact\": 1,\"missedImpact\": -1}}";
    private static final String TEST_RESULTS_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";

    private static final String TOOLTYPE_CHECKSTYLE = "checkStyle";
    private static final String TOOLTYPE_PIT = "pit";
    private static final String TOOLTYPE_COVERAGE = "coverage";
    private static final String TOOLTYPE_TEST_RESULTS = "test-results";
    private static final String TOOLTYPE_CSSLINT = "cssLint";

    /**
     * Verifies that the step skips all autograding parts if the configuration is empty.
     */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmpty() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, TOOLTYPE_CHECKSTYLE, "checkstyle", "{}");
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if testing has been requested, but no testing action has been recorded.
     */
    @Test
    public void shouldAbortBuildSinceNoTestActionHasBeenRegistered() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, TOOLTYPE_CHECKSTYLE, "checkstyle", AUTOGRADE_TESTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains("java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldGradeCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, TOOLTYPE_CHECKSTYLE, "checkstyle", AUTOGRADE_ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 40");

        assertGradingResult(baseline, 40);
    }

    /**
     * Verifies that Lint results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradeLintResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("csslint.xml");

        configureScanner(job, TOOLTYPE_CSSLINT, "csslint", AUTOGRADE_ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CssLint");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -228 (warnings distribution err:0, high:42, normal:9, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 0 of 100");

        assertGradingResult(baseline, 0);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResults() {
        String fileName = "test-successful.xml";
        WorkflowJob job = createPipelineWithWorkspaceFiles(fileName);

        configureScanner(job, TOOLTYPE_TEST_RESULTS, fileName, TEST_RESULTS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results ");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 2 - from recorded test results: 2, 2, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 2");

        assertGradingResult(baseline, 2);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionError() {
        String fileName = "test-assertion-error.xml";
        WorkflowJob job = createPipelineWithWorkspaceFiles(fileName);

        configureScanner(job, TOOLTYPE_TEST_RESULTS, fileName, TEST_RESULTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.UNSTABLE);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results ");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -10 - from recorded test results: 2, 0, 2, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 90");

        assertGradingResult(baseline, 90);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionErrorAndSkipTest() {
        String fileName = "test-assertion-error-with-skip.xml";
        WorkflowJob job = createPipelineWithWorkspaceFiles(fileName);

        configureScanner(job, TOOLTYPE_TEST_RESULTS, fileName, TEST_RESULTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.UNSTABLE);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results ");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -5 - from recorded test results: 3, 1, 1, 1");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 95");

        assertGradingResult(baseline, 95);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeCoverageResults() {
        String fileName = "coverage.xml";
        WorkflowJob job = createPipelineWithWorkspaceFiles(fileName);

        configureScanner(job, TOOLTYPE_COVERAGE, fileName, COVERAGE_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results ");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 100 - from recorded line coverage results: 100%");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 58 - from recorded branch coverage results: 79%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for coverage results: 100");

        assertGradingResult(baseline, 100);
    }

    /**
     * Verifies that Pit results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradePitResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");

        configureScanner(job, TOOLTYPE_PIT, "mutations", AUTOGRADE_MUTATION_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -39 - from recorded PIT mutation results: 15, 5, 10, 34");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for mutation coverage results: 61");

        assertGradingResult(baseline, 61);
    }

    /**
     * Returns the console log as a String.
     *
     * @param build the build to get the log for
     * @return the console log
     */
    protected String getConsoleLog(final Run<?, ?> build) {
        try {
            return JenkinsRule.getLog(build);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void configureScanner(final WorkflowJob job, final String toolType, final String fileName,
                                  final String configuration) {
        String pipeLineScript = "node {\n"
                + "  stage ('Integration Test') {\n";

        switch (toolType) {
            case TOOLTYPE_CHECKSTYLE:
            case TOOLTYPE_CSSLINT:
                pipeLineScript += "recordIssues tool: " + toolType + "(pattern: '**/" + fileName + "*')\n";
                break;
            case TOOLTYPE_PIT:
                pipeLineScript += "step([$class: 'PitPublisher', mutationStatsFile: '**/" + fileName + "*'])\n";
                break;
            case TOOLTYPE_COVERAGE:
                pipeLineScript += "publishCoverage adapters: [jacocoAdapter('**/" + fileName + "*')]\n";
                break;
            case TOOLTYPE_TEST_RESULTS:
                pipeLineScript += "junit testResults: '**/" + fileName + "'\n";
                break;

        }
        pipeLineScript += "autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}";

        job.setDefinition(new CpsFlowDefinition(pipeLineScript, true));
    }

    private void assertGradingResult(Run<?, ?> baseline, int gradingScore) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(gradingScore);
    }
}