package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import org.jenkinsci.plugins.pitmutation.PitPublisher;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.junit.JUnitResultArchiver;

import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.Cpd;
import io.jenkins.plugins.analysis.warnings.Pmd;
import io.jenkins.plugins.analysis.warnings.SpotBugs;
import io.jenkins.plugins.analysis.warnings.checkstyle.CheckStyle;
import io.jenkins.plugins.coverage.CoveragePublisher;
import io.jenkins.plugins.coverage.adapter.CoverageAdapter;
import io.jenkins.plugins.coverage.adapter.JacocoReportAdapter;
import io.jenkins.plugins.coverage.source.DefaultSourceFileResolver;
import io.jenkins.plugins.coverage.source.SourceFileResolver.SourceFileResolverLevel;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 * @author Kevin Richter
 * @author Simon Schönwiese
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {
    private static final String TESTS_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String COVERAGE_CONFIGURATION = "{\"coverage\": {\"maxScore\": 100, \"coveredImpact\": 1, \"missedImpact\": -1}}";
    private static final String PIT_CONFIGURATION = "{\"pit\": {\"maxScore\": 100, \"detectedImpact\": 1, \"undetectedImpact\": -1, \"ratioImpact\": 0}}";

    /** Verifies that the step skips all autograding parts if the configuration is empty. */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmpty() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", "{}");
        Run<?, ?> baseline = buildSuccessfully(job);

        assertSkipGradingIfConfigurationIsEmpty(baseline);
    }

    /** Verifies that the step skips all autograding parts if the configuration is empty. */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmptyFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/*checkstyle.xml");
        recorder.setTools(checkStyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader("{}"));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertSkipGradingIfConfigurationIsEmpty(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldSkipGradingIfConfigurationIsEmpty()#shouldSkipGradingIfConfigurationIsEmptyFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertSkipGradingIfConfigurationIsEmpty(final Run<?, ?> baseline) {
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
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", TESTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.FAILURE);

        assertAbortBuildSinceNoTestActionHasBeenRegistered(baseline);
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if testing has been requested, but no testing action
     * has been recorded
     */
    @Test
    public void shouldAbortBuildSinceNoTestActionHasBeenRegisteredFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/*checkstyle.xml");
        recorder.setTools(checkStyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(TESTS_CONFIGURATION));

        Run<?, ?> baseline = buildWithResult(project, Result.FAILURE);

        assertAbortBuildSinceNoTestActionHasBeenRegistered(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldAbortBuildSinceNoTestActionHasBeenRegistered()#shouldAbortBuildSinceNoTestActionHasBeenRegisteredFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertAbortBuildSinceNoTestActionHasBeenRegistered(final Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains(
                "java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertCountCheckStyleWarnings(baseline);
    }

    /**
     * Verifies that CheckStyle results are correctly graded (Freestyle)
     */
    @Test
    public void shouldCountCheckStyleWarningsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/*checkstyle.xml");
        recorder.setTools(checkStyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);
        assertCountCheckStyleWarnings(baseline);

    }

    /**
     * Provides Assertions for {@link #shouldCountCheckStyleWarnings()#shouldCountCheckStyleWarningsFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertCountCheckStyleWarnings(final Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 40");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(40);
    }

    /**
     * Verifies that Static Analysis results are correctly graded.
     */
    @Test
    public void shouldGradeStaticAnalysisResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("pmd.xml", "cpd.xml", "spotbugsXml.xml");
        configureScanner(job, "recordIssues", ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertGradeStaticAnalysisResults(baseline);
    }

    /**
     * Verifies that Static Analysis results are correctly graded.
     */
    @Test
    public void shouldGradeStaticAnalysisResultsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("pmd.xml", "cpd.xml", "spotbugsXml.xml");

        IssuesRecorder recorder = new IssuesRecorder();

        Pmd pmd = new Pmd();
        pmd.setPattern("**/*pmd.xml");

        Cpd cpd = new Cpd();
        cpd.setPattern("**/*cpd.xml");

        SpotBugs spotBugs = new SpotBugs();
        spotBugs.setPattern("**/*spotbugsXml.xml");

        recorder.setTools(pmd, cpd, spotBugs);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertGradeStaticAnalysisResults(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldGradeStaticAnalysisResults()#shouldGradeStaticAnalysisResultsFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertGradeStaticAnalysisResults(final Run<?, ?> baseline) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for PMD");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -2 (warnings distribution err:0, high:0, normal:1, low:0)");

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CPD");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score -7 (warnings distribution err:0, high:0, normal:0, low:7)");

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for SpotBugs");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 0 (warnings distribution err:0, high:0, normal:0, low:0)");

        assertThat(score).hasAchieved(91);
    }

    /**
     * Verifies that JUnit results are correctly graded.
     */
    @Test
    public void shouldGradeTestResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("TEST-InjectedTest.xml",
                "TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderITest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");
        configureScanner(job, "junit", TESTS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertGradeTestResults(baseline);
    }

    /**
     * Verifies that JUnit results are correctly graded.
     */
    @Test
    public void shouldGradeTestResultsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("TEST-InjectedTest.xml",
                "TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderITest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        JUnitResultArchiver jUnitResultArchiver = new JUnitResultArchiver("*");

        project.getPublishersList().add(jUnitResultArchiver);
        project.getPublishersList().add(new AutoGrader(TESTS_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertGradeTestResults(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldGradeTestResults()#shouldGradeTestResultsFreestyle()} Pipeline and
     * Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertGradeTestResults(final Run<?, ?> baseline) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results Test Result");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 60 - from recorded test results: 60, 60, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 60");

        assertThat(score).hasAchieved(60);
    }

    /**
     * Verifies that code coverage results are correctly graded.
     */
    @Test
    public void shouldGradeCodeCoverageResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");
        configureScanner(job, "jacoco", COVERAGE_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertGradeCodeCoverageResults(baseline);
    }

    /**
     * Verifies that code coverage results are correctly graded.
     */
    @Test
    public void shouldGradeCodeCoverageResultsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        JacocoReportAdapter jacocoReportAdapter = new JacocoReportAdapter("**/jacoco.xml");
        DefaultSourceFileResolver defaultSourceFileResolver = new DefaultSourceFileResolver(
                SourceFileResolverLevel.NEVER_STORE);

        CoveragePublisher coveragePublisher = new CoveragePublisher();
        List<CoverageAdapter> coverageAdapters = new ArrayList<>();
        coverageAdapters.add(jacocoReportAdapter);
        coveragePublisher.setAdapters(coverageAdapters);

        coveragePublisher.setSourceFileResolver(defaultSourceFileResolver);

        project.getPublishersList().add(coveragePublisher);
        project.getPublishersList().add(new AutoGrader(COVERAGE_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertGradeCodeCoverageResults(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldGradeCodeCoverageResults()#shouldGradeCodeCoverageResultsFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertGradeCodeCoverageResults(final Run<?, ?> baseline) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results Coverage Report");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 70 - from recorded line coverage results: 85%");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 8 - from recorded branch coverage results: 54%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for coverage results: 78");

        assertThat(score).hasAchieved(78);
    }

    /**
     * Verifies that PIT mutation coverage results are correctly graded.
     */
    @Test
    public void shouldGradePitMutationCoverageResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");
        configureScanner(job, "mutations", PIT_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertGradePitMutationCoverageResults(baseline);
    }

    /**
     * Verifies that PIT mutation coverage results are correctly graded. (Freestyle)
     */
    @Test
    public void shouldGradePitMutationCoverageResultsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");

        PitPublisher pitPublisher = new PitPublisher("**/*mutations.xml", 0, false);

        project.getPublishersList().add(pitPublisher);
        project.getPublishersList().add(new AutoGrader(PIT_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertGradePitMutationCoverageResults(baseline);
    }

    /**
     * Provides Assertions for {@link #shouldGradePitMutationCoverageResults()#shouldGradePitMutationCoverageResultsFreestyle()}
     * Pipeline and Freestyle Tests
     *
     * @param baseline:
     *         Build result including actions and console output.
     */
    private void assertGradePitMutationCoverageResults(final Run<?, ?> baseline) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] -> Score 73 - from recorded PIT mutation results: 191, 59, 132, 31");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for mutation coverage results: 73");

        assertThat(score).hasAchieved(73);
    }

    /**
     * Returns the console log as a String.
     *
     * @param build
     *         the build to get the log for
     *
     * @return the console log
     */
    protected String getConsoleLog(final Run<?, ?> build) {
        try {
            return JenkinsRule.getLog(build);
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void configureScanner(final WorkflowJob job, final String stepName, final String configuration) {
        StringBuilder pipelineScript = new StringBuilder("node {\n");
        pipelineScript.append("  stage ('Integration Test') {\n");

        switch (stepName) {
            case "mutations":
                pipelineScript.append(
                        "         step([$class: 'PitPublisher', mutationStatsFile: '**/mutations.xml'])\n");
                break;
            case "jacoco":
                pipelineScript.append(
                        "         publishCoverage adapters: [jacocoAdapter('**/jacoco.xml')], sourceFileResolver: sourceFiles('NEVER_STORE')\n");
                break;
            case "checkstyle":
                pipelineScript.append("         recordIssues tool: checkStyle(pattern: '**/checkstyle*')\n");
                break;
            case "recordIssues":
                pipelineScript.append("         recordIssues tools: [spotBugs(pattern: '**/spotbugsXml.xml'),\n");
                pipelineScript.append("                 pmdParser(pattern: '**/pmd.xml'),\n");
                pipelineScript.append("                 cpd(pattern: '**/cpd.xml')]\n");
                break;
            case "junit":
                pipelineScript.append("         junit testResults: '**/TEST-*.xml'\n");
                break;
            default:
                break;

        }

        pipelineScript.append("         autoGrade('").append(configuration).append("')\n");
        pipelineScript.append("  }\n");
        pipelineScript.append("}");
        job.setDefinition(new CpsFlowDefinition(pipelineScript.toString(), true));
    }

}