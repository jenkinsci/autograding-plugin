package io.jenkins.plugins.grading;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.Publisher;
import hudson.tasks.junit.JUnitResultArchiver;
import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.CssLint;
import io.jenkins.plugins.coverage.CoveragePublisher;
import io.jenkins.plugins.coverage.adapter.CoverageAdapter;
import io.jenkins.plugins.coverage.adapter.JacocoReportAdapter;
import io.jenkins.plugins.coverage.source.DefaultSourceFileResolver;
import io.jenkins.plugins.coverage.source.SourceFileResolver.SourceFileResolverLevel;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;
import org.jenkinsci.plugins.pitmutation.PitPublisher;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final String KOMBI_CONFIGURATUION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1},"
            + "\"pit\":{\"maxScore\":100,\"ratioImpact\":-1,\"detectedImpact\":0,\"undetectedImpact\":-1},"
            + "\"coverage\": {\"maxScore\": 100,\"coveredImpact\": 1,\"missedImpact\": -1},"
            + "\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}"
            + "}";

    private static final String TOOLTYPE_CHECKSTYLE = "checkStyle";
    private static final String TOOLTYPE_PIT = "pit";
    private static final String TOOLTYPE_COVERAGE = "coverage";
    private static final String TOOLTYPE_TEST_RESULTS = "test-results";
    private static final String TOOLTYPE_CSSLINT = "cssLint";

    private static final String CHECKSTYLE_FILE = "checkstyle.xml";
    private static final String CSSLINT_FILE = "csslint.xml";
    private static final String PIT_FILE = "mutations.xml";
    private static final String COVERAGE_FILE = "coverage.xml";
    private static final String TEST_FILE_SUCCESS = "test-successful.xml";
    private static final String TEST_FILE_ERROR = "test-assertion-error.xml";
    private static final String TEST_FILE_ERROR_SKIP = "test-assertion-error-with-skip.xml";

    private static final String PATTERN_PREFIX = "**/";

    /**
     * Verifies that the step skips all autograding parts if the configuration is empty.
     */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmpty() {
        Run<?, ?> baseline = buildJob(CHECKSTYLE_FILE, TOOLTYPE_CHECKSTYLE, "{}");

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if testing has been requested, but no testing action
     * has been recorded.
     */
    @Test
    public void shouldAbortBuildSinceNoTestActionHasBeenRegistered() {
        Run<?, ?> baseline = buildJobWithResult(CHECKSTYLE_FILE, TOOLTYPE_CHECKSTYLE, AUTOGRADE_TESTS_CONFIGURATION,
                Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains(
                "java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldGradeCheckStyleWarnings() {
        Run<?, ?> baseline = buildJob(CHECKSTYLE_FILE, TOOLTYPE_CHECKSTYLE, AUTOGRADE_ANALYSIS_CONFIGURATION);

        assertTestResults(baseline, "[Autograding] Grading static analysis results for CheckStyle",
                "[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)",
                "[Autograding] Total score for static analysis results: 40", 40);
    }

    /**
     * Verifies that Lint results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradeLintResults() {
        Run<?, ?> baseline = buildJob(CSSLINT_FILE, TOOLTYPE_CSSLINT, AUTOGRADE_ANALYSIS_CONFIGURATION);

        assertLintResults(baseline);
    }

    /**
     * Verifies that Lint results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradeLintResultsFreestyle() {
        Run<?, ?> baseline = buildFreeStyleProject(CSSLINT_FILE, createLintPublisher(), AUTOGRADE_ANALYSIS_CONFIGURATION);

        assertLintResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResults() {
        Run<?, ?> baseline = buildJob(TEST_FILE_SUCCESS, TOOLTYPE_TEST_RESULTS, TEST_RESULTS_CONFIGURATION);

        assertTestsSuccesfulResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsInFreeStyle() {
        Run<?, ?> baseline = buildFreeStyleProject(TEST_FILE_SUCCESS, createTestsPublisher(TEST_FILE_SUCCESS),
                TEST_RESULTS_CONFIGURATION);

        assertTestsSuccesfulResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionError() {
        Run<?, ?> baseline = buildJobWithResult(TEST_FILE_ERROR, TOOLTYPE_TEST_RESULTS, TEST_RESULTS_CONFIGURATION,
                Result.UNSTABLE);

        assertTestsErrorResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionErrorInFreeStyle() {
        Run<?, ?> baseline = buildFreeStyleProjectWithResult(TEST_FILE_ERROR, createTestsPublisher(TEST_FILE_ERROR),
                TEST_RESULTS_CONFIGURATION, Result.UNSTABLE);

        assertTestsErrorResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionErrorAndSkipTest() {
        Run<?, ?> baseline = buildJobWithResult(TEST_FILE_ERROR_SKIP, TOOLTYPE_TEST_RESULTS, TEST_RESULTS_CONFIGURATION,
                Result.UNSTABLE);

        assertTestsErrorSkipResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeTestResultsWithAssertionErrorAndSkipTestInFreeStyle() {
        Run<?, ?> baseline = buildFreeStyleProjectWithResult(TEST_FILE_ERROR_SKIP, createTestsPublisher(TEST_FILE_ERROR_SKIP),
                TEST_RESULTS_CONFIGURATION, Result.UNSTABLE);

        assertTestsErrorSkipResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeCoverage() {
        Run<?, ?> baseline = buildJob(COVERAGE_FILE, TOOLTYPE_COVERAGE, COVERAGE_CONFIGURATION);

        assertCoverageResults(baseline);
    }

    /**
     * @author Patrick Rogg
     */
    @Test
    public void shouldGradeCoverageInFreeStyle() {
        CoveragePublisher coveragePublisher = createCoveragePublisher();
        Run<?, ?> baseline = buildFreeStyleProject(COVERAGE_FILE, coveragePublisher, COVERAGE_CONFIGURATION);

        assertCoverageResults(baseline);
    }

    /**
     * Verifies that Pit results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradePitResults() {
        Run<?, ?> baseline = buildJob(PIT_FILE, TOOLTYPE_PIT, AUTOGRADE_MUTATION_CONFIGURATION);

        assertPitResults(baseline);
    }

    /**
     * Verifies that Pit results are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradePitResultsFreeStyle() {
        Run<?, ?> baseline = buildFreeStyleProject(PIT_FILE, createPitPublisher(), AUTOGRADE_MUTATION_CONFIGURATION);

        assertPitResults(baseline);
    }

    /**
     * Verifies that results from all 4 metrics are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradeKombiResults() {

        WorkflowJob job = createPipelineWithWorkspaceFiles(CSSLINT_FILE, COVERAGE_FILE, PIT_FILE, TEST_FILE_SUCCESS);
        configureScannerForAll(job);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertLintResultsWithTotalScore(baseline, 163);
        assertTestsSuccesfulResultsWithTotalScore(baseline, 163);
        assertCoverageResultsWithTotalScore(baseline, 163);
        assertPitResultsWithTotalScore(baseline, 163);
    }

    /**
     * Verifies that results from all 4 metrics are correctly graded.
     *
     * @author Andreas Stiglmeier
     */
    @Test
    public void shouldGradeKombiResultsFreeStyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(CSSLINT_FILE, COVERAGE_FILE, PIT_FILE, TEST_FILE_SUCCESS);

        project.getPublishersList().add(createLintPublisher());
        project.getPublishersList().add(createCoveragePublisher());
        project.getPublishersList().add(createPitPublisher());
        project.getPublishersList().add(createTestsPublisher(TEST_FILE_SUCCESS));
        project.getPublishersList().add(new AutoGrader(KOMBI_CONFIGURATUION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertLintResultsWithTotalScore(baseline, 163);
        assertTestsSuccesfulResultsWithTotalScore(baseline, 163);
        assertCoverageResultsWithTotalScore(baseline, 163);
        assertPitResultsWithTotalScore(baseline, 163);
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
                pipeLineScript += "recordIssues tool: " + toolType + "(pattern: '" + PATTERN_PREFIX + fileName + "*')\n";
                break;
            case TOOLTYPE_PIT:
                pipeLineScript += "step([$class: 'PitPublisher', mutationStatsFile: '" + PATTERN_PREFIX + fileName + "*'])\n";
                break;
            case TOOLTYPE_COVERAGE:
                pipeLineScript += "publishCoverage adapters: [jacocoAdapter('" + PATTERN_PREFIX + fileName + "*')]\n";
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

    private void configureScannerForAll(final WorkflowJob job) {

        String pipeLineScript = "node {\n"
                + "  stage ('Integration Test') {\n"
                + "recordIssues tool: cssLint(pattern: '**/" + CSSLINT_FILE + "*')\n"
                + "step([$class: 'PitPublisher', mutationStatsFile: '**/" + PIT_FILE + "*'])\n"
                + "publishCoverage adapters: [jacocoAdapter('**/" + COVERAGE_FILE + "*')]\n"
                + "junit testResults: '**/" + TEST_FILE_SUCCESS + "'\n"
                + "autoGrade('" + KOMBI_CONFIGURATUION + "')\n"
                + "  }\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipeLineScript, true));
    }

    private Run<?, ?> buildJob(final String fileName, final String toolType, final String configuration) {
        return buildSuccessfully(configureJob(fileName, toolType, configuration));
    }

    private Run<?, ?> buildJobWithResult(final String fileName, final String toolType, final String configuration,
                                         final Result result) {
        return buildWithResult(configureJob(fileName, toolType, configuration), result);
    }

    private WorkflowJob configureJob(final String fileName, final String toolType, final String configuration) {
        WorkflowJob job = createPipelineWithWorkspaceFiles(fileName);
        configureScanner(job, toolType, fileName, configuration);
        return job;
    }

    private Run<?, ?> buildFreeStyleProject(final String fileName, final Publisher publisher,
                                            final String configuration) {
        return buildSuccessfully(configureProject(fileName, publisher, configuration));
    }

    private Run<?, ?> buildFreeStyleProjectWithResult(final String fileName, final Publisher publisher,
                                                      final String configuration, Result result) {
        return buildWithResult(configureProject(fileName, publisher, configuration), result);
    }

    private FreeStyleProject configureProject(final String fileName, final Publisher publisher,
                                              final String configuration) {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(fileName);
        project.getPublishersList().add(publisher);
        project.getPublishersList().add(new AutoGrader(configuration));
        return project;
    }

    private CoveragePublisher createCoveragePublisher() {
        JacocoReportAdapter jacocoReportAdapter = new JacocoReportAdapter(PATTERN_PREFIX + COVERAGE_FILE);
        DefaultSourceFileResolver defaultSourceFileResolver = new DefaultSourceFileResolver(
                SourceFileResolverLevel.NEVER_STORE);

        List<CoverageAdapter> coverageAdapters = new ArrayList<>();
        coverageAdapters.add(jacocoReportAdapter);

        CoveragePublisher coveragePublisher = new CoveragePublisher();
        coveragePublisher.setAdapters(coverageAdapters);
        coveragePublisher.setSourceFileResolver(defaultSourceFileResolver);
        return coveragePublisher;
    }

    private IssuesRecorder createLintPublisher() {
        IssuesRecorder issuesRecorder = new IssuesRecorder();
        CssLint cssLint = new CssLint();
        cssLint.setPattern(PATTERN_PREFIX + CSSLINT_FILE);
        issuesRecorder.setTools(cssLint);
        return issuesRecorder;
    }

    private PitPublisher createPitPublisher() {
        return new PitPublisher(PIT_FILE, 0, false);
    }

    private JUnitResultArchiver createTestsPublisher(final String fileName) {
        return new JUnitResultArchiver(fileName);
    }

    private void assertLintResults(final Run<?, ?> baseline) {
        assertLintResultsWithTotalScore(baseline, 0);
    }

    private void assertLintResultsWithTotalScore(final Run<?, ?> baseline, final int finalScore) {
        assertTestResults(baseline, "[Autograding] Grading static analysis results for CssLint",
                "[Autograding] -> Score -228 (warnings distribution err:0, high:42, normal:9, low:0)",
                "[Autograding] Total score for static analysis results: 0 of 100", finalScore);
    }

    private void assertTestsSuccesfulResults(final Run<?, ?> baseline) {
        assertTestsSuccesfulResultsWithTotalScore(baseline, 2);
    }

    private void assertTestsSuccesfulResultsWithTotalScore(final Run<?, ?> baseline, final int finalScore) {
        assertTestResults(baseline, "[Autograding] Grading test results ",
                "[Autograding] -> Score 2 - from recorded test results: 2, 2, 0, 0",
                "[Autograding] Total score for test results: 2", finalScore);
    }

    private void assertTestsErrorResults(final Run<?, ?> baseline) {
        assertTestResults(baseline, "[Autograding] Grading test results ",
                "[Autograding] -> Score -10 - from recorded test results: 2, 0, 2, 0",
                "[Autograding] Total score for test results: 90", 90);
    }

    private void assertTestsErrorSkipResults(final Run<?, ?> baseline) {
        assertTestResults(baseline, "[Autograding] Grading test results ",
                "[Autograding] -> Score -5 - from recorded test results: 3, 1, 1, 1",
                "[Autograding] Total score for test results: 95", 95);
    }

    private void assertCoverageResults(final Run<?, ?> baseline) {
        assertCoverageResultsWithTotalScore(baseline, 100);
    }

    private void assertCoverageResultsWithTotalScore(final Run<?, ?> baseline, final int finalScore) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results ");
        assertTestResults(baseline, "[Autograding] -> Score 100 - from recorded line coverage results: 100%",
                "[Autograding] -> Score 58 - from recorded branch coverage results: 79%",
                "[Autograding] Total score for coverage results: 100", finalScore);
    }

    private void assertPitResults(final Run<?, ?> baseline) {
        assertPitResultsWithTotalScore(baseline, 61);
    }

    private void assertPitResultsWithTotalScore(final Run<?, ?> baseline, final int finalScore) {
        assertTestResults(baseline, "[Autograding] Grading PIT mutation results PIT Mutation Report",
                "[Autograding] -> Score -39 - from recorded PIT mutation results: 15, 5, 10, 34",
                "[Autograding] Total score for mutation coverage results: 61", finalScore);
    }

    private void assertTestResults(final Run<?, ?> baseline, final String firstLine, final String secondLine, final String thirdLine,
                                   final int totalResult) {
        assertThat(getConsoleLog(baseline)).contains(firstLine);
        assertThat(getConsoleLog(baseline)).contains(
                secondLine);
        assertThat(getConsoleLog(baseline)).contains(thirdLine);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(totalResult);
    }
}