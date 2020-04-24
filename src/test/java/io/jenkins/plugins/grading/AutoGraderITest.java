package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import edu.hm.hafner.analysis.IssueParser;

import junit.framework.TestResult;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.junit.JUnitResultArchiver;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResultAction.Data;

import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.JUnit;
import io.jenkins.plugins.analysis.warnings.checkstyle.CheckStyle;
import io.jenkins.plugins.coverage.CoveragePublisher;
import io.jenkins.plugins.coverage.adapter.JacocoReportAdapter;
import io.jenkins.plugins.coverage.targets.Ratio;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 * @author Johannes Hintermaier
 * @author Lion Kosiuk
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {
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

        configureScanner(job, "checkstyle", "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}");
        Run<?, ?> baseline = buildWithResult(job, Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains("java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}");
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
     * Verifies that SpotBugs results are correctly graded.
     */
    @Test
    public void shouldGradeSpotBugs(){
        WorkflowJob job = createPipelineWithWorkspaceFiles("spotbugs.xml");

        configureScanner(job, "spotbugs", "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(97);
    }

    /**
     * Verifies that CPD results are correctly graded.
     */
    @Test
    public void shouldGradeCPD(){
        WorkflowJob job = createPipelineWithWorkspaceFiles("cpd.xml");

        configureScanner(job, "cpd", "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(98);
    }

    /**
     * Verifies that PMD results are correctly graded.
     */
    @Test
    public void shouldGradePMD(){
        WorkflowJob job = createPipelineWithWorkspaceFiles("pmd.xml");

        configureScanner(job, "pmd", "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(88);
    }

    /**
     * Verifies that Mutation results are correctly graded.
     */
    @Test
    public void shouldGradeMutationCoverage() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");

        configureScanner(job, "mutations",
                "{\"pit\":{\"maxScore\":100,\"detectedImpact\":1,\"undetectedImpact\":-1,\"ratioImpact\":0}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(56);
    }

    /**
     * Verifies that Tests results are correctly graded with pipeline.
     */
    @Test
    public void shouldGradeTestResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("TEST-InjectedTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderITest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        configureScanner(job, "*",
                "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        testResultsAssertions(baseline);
    }

    /**
     * Verifies that Tests results are correctly graded with freestyle.
     */
    @Test
    public void shouldGradeTestResultsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("TEST-InjectedTest.xml",
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
        project.getPublishersList().add(new AutoGrader("{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}"));

        Run<?, ?> run = buildSuccessfully(project);

        testResultsAssertions(run);
    }

    private void testResultsAssertions(Run<?, ?> run) {
        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(53);
    }

    /**
     * Verifies that Coveragescore results are correctly graded with pipeline.
     */
    @Test
    public void shouldGradeCoverageScore() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");

        configureScanner(job, "jacoco", "{ \"coverage\":{\"maxScore\":100,\"coveredImpact\":1,\"missedImpact\":-1}}");
        Run<?, ?> baseline = buildSuccessfully(job);

        jacocoBugAssertions(baseline);
    }

    /**
     * Verifies that Coveragescore results are correctly graded with freestyle.
     */
    @Test
    public void shouldGraveCoverageScoreFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        JacocoReportAdapter jacocoReportAdapter = new JacocoReportAdapter("**/jacoco.xml*");
        CoveragePublisher coveragePublisher = new CoveragePublisher();
        coveragePublisher.setAdapters(Collections.singletonList(jacocoReportAdapter));

        project.getPublishersList().add(coveragePublisher);
        project.getPublishersList().add(new AutoGrader("{ \"coverage\":{\"maxScore\":100,\"coveredImpact\":1,\"missedImpact\":-1}}"));

        Run<?, ?> run = buildSuccessfully(project);

        jacocoBugAssertions(run);
    }

    private void jacocoBugAssertions(Run<?, ?> run) {
        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(50);
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


    private void configureScanner(final WorkflowJob job, final String fileName,
                                  final String configuration) {
        String script = "node {\n";
        switch (fileName) {
            case "checkstyle":
                script += "  stage ('Integration Test') {\n"
                        + "         recordIssues tool: checkStyle(pattern: '**/" + fileName + "*')\n";
                break;
            case "jacoco":
                script += "  stage ('Integration Test Coverage') {\n"
                        + "         publishCoverage adapters: [jacocoAdapter('**/" + fileName
                        + "*')], sourceFileResolver: sourceFiles('NEVER_STORE')\n";
                break;
            case "mutations":
                script += "  stage ('Integration Test Mutation Coverage') {\n"
                        + "         step([$class: 'PitPublisher', mutationStatsFile: '**/" + fileName + "*'])\n";
                break;
            case "spotbugs":
                script += "  stage ('Integration Test SpotBugs') {\n"
                        + "         recordIssues tool: spotBugs(pattern: '**/" + fileName + "*')\n";
                break;
            case "cpd":
                script += "  stage ('Integration Test CPD') {\n"
                        + "         recordIssues tool: cpd(pattern: '**/" + fileName + "*')\n";
                break;
            case "pmd":
                script += "  stage ('Integration Test PMD') {\n"
                        + "         recordIssues tool: pmdParser(pattern: '**/" + fileName + "*')\n";
                break;

            default:
                script += "  stage ('Build and Static Analysis') {\n"
                        + "         junit testResults: '**/TEST-" + fileName + ".xml'\n";
                break;
        }
        script += "         autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(script, true));
    }

}