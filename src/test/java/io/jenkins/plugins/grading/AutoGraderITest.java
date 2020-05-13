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
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {

    private static final String ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String TESTS_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String COVERAGE_CONFIGURATION = "{\"coverage\":{\"maxScore\":100,\"coveredImpact\":1,\"missedImpact\":-1}}";
    private static final String PIT_CONFIGURATION = "{\"pit\": {\"maxScore\": 100,\"detectedImpact\": 1,\"undetectedImpact\": -1,\"ratioImpact\": 0}}";


    /** Verifies that the step skips all autograding parts if the configuration is empty. */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmpty() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", "{}");
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

        configureScanner(job, "checkstyle", TESTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains("java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 40");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(40);
    }

    /**
     * Verifies that jUnit results are graded with a score of 94.
     */
    @Test
    public void shouldGradeTestScoreWith94() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jUnit.xml");

        configureScanner(job, "jUnit", TESTS_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.UNSTABLE);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(94);
    }


    /**
     * Verifies that Mutations results are graded with a score of 73.
     */
    @Test
    public void shouldGradeMutationsScoreWith73() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");
        configureScanner(job, "mutations", PIT_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(73);
    }

    /* ---- Freestyle - Tests --- */


    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleInFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(
                "checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("checkstyle.xml");
        recorder.setTools(checkStyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(run)).contains("[Autograding] Total score for static analysis results: 40");
    }

    /**
     * Verifies that jUnit results are graded with a score of 94.
     */
    @Test
    public void shouldGradeTestScoreInFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(
                "jUnit.xml");

        JUnitResultArchiver jUnitResultArchiver = new JUnitResultArchiver("jUnit.xml");

        project.getPublishersList().add(jUnitResultArchiver);
        project.getPublishersList().add(new AutoGrader(TESTS_CONFIGURATION));

        Run<?, ?> run = buildWithResult(project, Result.UNSTABLE);

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(94);
    }

    /**
     * Verifies that Coverage is correctly graded.
    */
    @Test
    public void shouldGradeCodeCoverageInFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        JacocoReportAdapter jacocoReportAdapter = new JacocoReportAdapter("jacoco.xml");
        DefaultSourceFileResolver defaultSourceFileResolver = new DefaultSourceFileResolver(
                SourceFileResolverLevel.NEVER_STORE);

        List<CoverageAdapter> coverageAdapters = new ArrayList<>();
        coverageAdapters.add(jacocoReportAdapter);

        CoveragePublisher coveragePublisher = new CoveragePublisher();
        coveragePublisher.setAdapters(coverageAdapters);
        coveragePublisher.setSourceFileResolver(defaultSourceFileResolver);

        project.getPublishersList().add(coveragePublisher);
        project.getPublishersList().add(new AutoGrader(COVERAGE_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(100);
    }

    /**
     * Verifies that Mutations results are graded with a score of 73.
     */
    @Test
    public void shouldGradeMutationsInFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(
                "mutations.xml");

        PitPublisher pitPublisher = new PitPublisher("mutations.xml", 50, false);

        project.getPublishersList().add(pitPublisher);
        project.getPublishersList().add(new AutoGrader(PIT_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);
        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
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

    /**
     * Configures Scanner for correct file.
     *
     * @param job
     *         the job to set the definition for.
     * @param fileName
     *         the fileName to get the results from
     * @param configuration
     *         the test configuration String
     *
     */
    private void configureScanner(final WorkflowJob job, final String fileName, final String configuration) {
        String script = "node {\n";
        switch(fileName) {
            case "jUnit":
                script += "  stage ('Build and Static Analysis') {\n"
                        + "         junit testResults: '**/" + fileName + ".xml'\n";
                break;
            case "checkstyle":
                script += "  stage ('Integration Test') {\n"
                        + "         recordIssues tool: checkStyle(pattern: '**/" + fileName + "*')\n";
                break;
            case "mutations":
                script += "  stage ('Test Mutation Coverage') {\n"
                        + "         step([$class: 'PitPublisher', mutationStatsFile: '**/" + fileName + "*'])\n";
                break;
            case "jacoco":
                script += " stage ('Code coverage Analysis') {\n"
                        + "         publishCoverage adapters: [jacocoAdapter('**/" + fileName
                        + "*')], sourceFileResolver: sourceFiles('NEVER_STORE')\n";
                break;
            default:
                break;
        }

        script += "         autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(script, true));
    }
}
