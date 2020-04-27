package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;

import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.Cpd;
import io.jenkins.plugins.analysis.warnings.JUnit;
import io.jenkins.plugins.analysis.warnings.Pit;
import io.jenkins.plugins.analysis.warnings.Pmd;
import io.jenkins.plugins.analysis.warnings.SpotBugs;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {

    // Extracted from Readme
    private static final String SCANNER_TEST_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String SCANNER_ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String SCANNER_COVERAGE_CONFIGURATION = "{\"coverage\": {\"maxScore\": 100, \"coveredImpact\": 1, \"missedImpact\": -1}}";
    private static final String SCANNER_PIT_CONFIGURATION = "{\"pit\": {\"maxScore\": 100, \"detectedImpact\": 1, \"undetectedImpact\": -1, \"ratioImpact\": 0}}";

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

        configureScanner(job, "checkstyle", SCANNER_TEST_CONFIGURATION);
        Run<?, ?> baseline = buildWithResult(job, Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains("java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", SCANNER_ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 40");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(40);
    }

    @Test
    public void shouldGradeAnalysisWithScoreOf85() {
        // Pipeline
        WorkflowJob pipelineJob = createPipelineWithWorkspaceFiles("pmd.xml", "cpd.xml", "spotbugsXml.xml");
        configureScanner(pipelineJob, "recordIssues", SCANNER_ANALYSIS_CONFIGURATION);

        // Free style
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("pmd.xml", "cpd.xml", "spotbugsXml.xml");
        IssuesRecorder recorder = new IssuesRecorder();

        Pmd pmd = new Pmd();
        Cpd cpd = new Cpd();
        SpotBugs spotBugs = new SpotBugs();
        recorder.setTools(pmd, cpd, spotBugs);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(SCANNER_ANALYSIS_CONFIGURATION));

        buildPipelineAndFreestyleJob(pipelineJob, project);
    }

    private void buildPipelineAndFreestyleJob(WorkflowJob pipelineJob, FreeStyleProject freestyleJob) {
        Run<?, ?> freestyle = buildSuccessfully(freestyleJob);
        Run<?, ?> pipeline = buildSuccessfully(pipelineJob);

        // Assert
        assertAchievedScore(pipeline, 85);
        assertGradeAnalysis(pipeline);
        assertGradeAnalysis(freestyle);
    }

    private void assertAchievedScore(Run<?, ?> pipeLineJob, int scoreToBeAsserted) {
        List<AutoGradingBuildAction> actions = pipeLineJob.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(scoreToBeAsserted);
    }

    private void assertGradeAnalysis(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for PMD");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -8 (warnings distribution err:0, high:0, normal:4, low:0)");

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CPD");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -7 (warnings distribution err:0, high:0, normal:0, low:7)");

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for SpotBugs");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 0 (warnings distribution err:0, high:0, normal:0, low:0)");
    }

    @Test
    public void shouldGradeTestResultsWithScoreOf61() {
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
        configureScanner(job, "junit", SCANNER_TEST_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results Test Result");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 61 - from recorded test results: 61, 61, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 61");

        assertThat(score).hasAchieved(61);
    }

    @Test
    public void shouldGradeCoverageWithScoreOf100() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");
        configureScanner(job, "jacoco", SCANNER_COVERAGE_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results Coverage Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 76 - from recorded line coverage results: 88%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 24 - from recorded branch coverage results: 62%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for coverage results: 100");

        assertThat(score).hasAchieved(100);
    }

    @Test
    public void shouldGradePitMutationWithScoreOf87() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");
        configureScanner(job, "mutations", SCANNER_PIT_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 87 - from recorded PIT mutation results: 191, 52, 139, 28");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for mutation coverage results: 87");

        assertThat(score).hasAchieved(87);
    }

    @Test
    public void shouldGradePitMutationAsFreeStyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");

        IssuesRecorder recorder = new IssuesRecorder();

        Pit pit = new Pit();
        pit.setPattern("**/mutations.xml");
        recorder.setTools(pit);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(SCANNER_PIT_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 87 - from recorded PIT mutation results: 191, 52, 139, 28");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for mutation coverage results: 87");
    }

    @Test
    public void shouldGradeTestResultMutationAsFreestyle() {
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
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");;

        IssuesRecorder recorder = new IssuesRecorder();

        JUnit pit = new JUnit();
        pit.setPattern("**/TEST-*.xml");
        recorder.setTools(pit);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(SCANNER_TEST_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(project);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results Test Result");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 61 - from recorded test results: 61, 61, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for test results: 61");
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
                pipelineScript.append("         step([$class: 'PitPublisher', mutationStatsFile: '**/mutations.xml'])\n");
                break;
            case "jacoco":
                pipelineScript.append("         publishCoverage adapters: [jacocoAdapter('**/jacoco.xml')], sourceFileResolver: sourceFiles('NEVER_STORE')\n");
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

        };

        pipelineScript.append("         autoGrade('").append(configuration).append("')\n");
        pipelineScript.append("  }\n");
        pipelineScript.append("}");
        job.setDefinition(new CpsFlowDefinition(pipelineScript.toString(), true));
    }

}
