package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.AnalysisConfiguration;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.grading.CoverageConfiguration;
import edu.hm.hafner.grading.PitConfiguration;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.TestScore;

import org.jenkinsci.plugins.pitmutation.PitPublisher;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.FreeStyleProject;
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
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {
    private static final String TEST_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String COVERAGE_CONFIGURATION = "{\"coverage\": {\"maxScore\": 100, \"coveredPercentageImpact\": 1, \"missedPercentageImpact\": -1}}";
    private static final String PIT_CONFIGURATION = "{\"pit\": {\"maxScore\": 100, \"detectedImpact\": 1, \"undetectedImpact\": -1, \"detectedPercentageImpact\": 0, \"undetectedPercentageImpact\": 0}}";
    private static final String[] TEST_REPORTS = {"TEST-InjectedTest.xml",
            "TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
            "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
            "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
            "TEST-io.jenkins.plugins.grading.AutoGraderITest.xml",
            "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
            "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
            "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
            "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
            "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
            "TEST-io.jenkins.plugins.grading.TestScoreTest.xml"};
    private static final String[] ANALYSIS_REPORTS = {"pmd.xml", "cpd.xml", "spotbugsXml.xml"};

    /** Verifies that the step skips all autograding parts if the configuration is empty. */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmpty() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", "{}");
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping code coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");
    }

    /**
     * Verifies that an error will be reported if testing has been requested, but no testing action has been recorded.
     */
    @Test
    public void shouldAbortBuildSinceNoTestActionHasBeenRegistered() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", TEST_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Grading test results",
                "[Autograding] [-ERROR-] -> Scoring of test results has been enabled, but no results have been found.");
    }

    /**
     * Verifies that a single analysis result will be correctly added.
     */
    @Test
    public void shouldCountCheckStyleWarnings() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");
        configureScanner(job, "checkstyle", ANALYSIS_CONFIGURATION);

        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/*checkstyle.xml");
        recorder.setTools(checkStyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> freestyle = buildSuccessfully(project);
        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(freestyle, 40);
        assertAchievedScore(pipeline, 40);
        assertCheckStyle(freestyle);
        assertCheckStyle(pipeline);
    }

    private void assertCheckStyle(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);
        AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration.AnalysisConfigurationBuilder()
                .setMaxScore(100)
                .setErrorImpact(-10)
                .setHighImpact(-5)
                .setNormalImpact(-2)
                .setLowImpact(-1)
                .build();

        assertThat(score).hasAnalysisConfiguration(analysisConfiguration);
        assertThat(score).hasAnalysisAchieved(40);

        AnalysisScore aScore = score.getAnalysisScores().get(0);
        assertThat(aScore).hasId("checkstyle");
        assertThat(aScore).hasErrorsSize(6);
        assertThat(aScore).hasHighSeveritySize(0);
        assertThat(aScore).hasNormalSeveritySize(0);
        assertThat(aScore).hasLowSeveritySize(0);
        assertThat(aScore).hasTotalSize(6);
        assertThat(aScore).hasTotalImpact(-60);
    }

    /**
     * Verifies that multiple analysis results will be correctly summed.
     */
    @Test
    public void shouldGradeMultipleAnalysisResults() {
        WorkflowJob pipelineJob = createPipelineWithWorkspaceFiles(ANALYSIS_REPORTS);
        configureScanner(pipelineJob, "recordIssues", ANALYSIS_CONFIGURATION);

        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(ANALYSIS_REPORTS);
        IssuesRecorder recorder = new IssuesRecorder();

        Pmd pmd = new Pmd();
        Cpd cpd = new Cpd();
        SpotBugs spotBugs = new SpotBugs();
        recorder.setTools(pmd, cpd, spotBugs);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> freestyle = buildSuccessfully(project);
        Run<?, ?> pipeline = buildSuccessfully(pipelineJob);

        assertAchievedScore(freestyle, 85);
        assertAchievedScore(pipeline, 85);
        assertGradeAnalysis(freestyle);
        assertGradeAnalysis(pipeline);
    }

    private void assertGradeAnalysis(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Grading static analysis results",
                "[Autograding] -> PMD score: -8 (errors:0, high:0, normal:4, low:0)",
                "[Autograding] -> CPD score: -7 (errors:0, high:0, normal:0, low:7)",
                "[Autograding] -> SpotBugs score: 0 (errors:0, high:0, normal:0, low:0)",
                "[Autograding] Total score for static analysis results: 85 of 100");

        List<AnalysisScore> analysisScores = score.getAnalysisScores();
        analysisScores.sort(Comparator.comparing(AnalysisScore::getId));

        AnalysisScore cpd = analysisScores.get(0);
        assertThat(cpd).hasId("cpd");
        assertThat(cpd).hasErrorsSize(0);
        assertThat(cpd).hasHighSeveritySize(0);
        assertThat(cpd).hasNormalSeveritySize(0);
        assertThat(cpd).hasLowSeveritySize(7);
        assertThat(cpd).hasTotalSize(7);
        assertThat(cpd).hasTotalImpact(-7);

        AnalysisScore pmd = analysisScores.get(1);
        assertThat(pmd).hasId("pmd");
        assertThat(pmd).hasErrorsSize(0);
        assertThat(pmd).hasHighSeveritySize(0);
        assertThat(pmd).hasNormalSeveritySize(4);
        assertThat(pmd).hasLowSeveritySize(0);
        assertThat(pmd).hasTotalSize(4);
        assertThat(pmd).hasTotalImpact(-8);

        AnalysisScore spotbugs = analysisScores.get(2);
        assertThat(spotbugs).hasId("spotbugs");
        assertThat(spotbugs).hasErrorsSize(0);
        assertThat(spotbugs).hasHighSeveritySize(0);
        assertThat(spotbugs).hasNormalSeveritySize(0);
        assertThat(spotbugs).hasLowSeveritySize(0);
        assertThat(spotbugs).hasTotalSize(0);
        assertThat(spotbugs).hasTotalImpact(0);
    }

    /**
     * Verifies that multiple test results will be correctly summed.
     */
    @Test
    public void shouldGradeMultipleTestResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles(TEST_REPORTS);
        configureScanner(job, "junit", TEST_CONFIGURATION);

        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(TEST_REPORTS);
        JUnitResultArchiver jUnitResultArchiver = new JUnitResultArchiver("*");
        project.getPublishersList().add(jUnitResultArchiver);
        project.getPublishersList().add(new AutoGrader(TEST_CONFIGURATION));

        Run<?, ?> freestyle = buildSuccessfully(project);
        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(freestyle, 61);
        assertAchievedScore(pipeline, 61);
        assertTestResults(freestyle);
        assertTestResults(pipeline);
    }

    private void assertTestResults(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);

        TestScore testScore = score.getTestScores().get(0);
        assertThat(testScore).hasPassedSize(61);
        assertThat(testScore).hasTotalSize(61);
        assertThat(testScore).hasFailedSize(0);
        assertThat(testScore).hasSkippedSize(0);
    }

    /**
     * Verifies that coverage results will be correctly scored.
     */
    @Test
    public void shouldGradeCoverageWithScoreOf100() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");
        configureScanner(job, "jacoco", COVERAGE_CONFIGURATION);

        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        JacocoReportAdapter jacocoReportAdapter = new JacocoReportAdapter("**/jacoco.xml");
        DefaultSourceFileResolver defaultSourceFileResolver = new DefaultSourceFileResolver(
                SourceFileResolverLevel.NEVER_STORE);

        List<CoverageAdapter> coverageAdapters = new ArrayList<>();
        coverageAdapters.add(jacocoReportAdapter);

        CoveragePublisher coveragePublisher = new CoveragePublisher();
        coveragePublisher.setAdapters(coverageAdapters);
        coveragePublisher.setSourceFileResolver(defaultSourceFileResolver);

        project.getPublishersList().add(coveragePublisher);
        project.getPublishersList().add(new AutoGrader(COVERAGE_CONFIGURATION));

        Run<?, ?> pipeline = buildSuccessfully(job);
        Run<?, ?> freestyle = buildSuccessfully(project);

        assertAchievedScore(pipeline, 100);
        assertAchievedScore(freestyle, 100);
        assertGradeCoverage(freestyle);
        assertGradeCoverage(pipeline);
    }

    private void assertGradeCoverage(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);
        CoverageConfiguration coverageConfiguration = new CoverageConfiguration.CoverageConfigurationBuilder()
                .setCoveredPercentageImpact(1)
                .setMissedPercentageImpact(-1)
                .setMaxScore(100)
                .build();

        assertThat(score).hasCoverageConfiguration(coverageConfiguration);
    }

    /**
     * Verifies that PIT results will be correctly scored.
     */
    @Test
    public void shouldGradePitMutationWithScoreOf87() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");
        configureScanner(job, "mutations", PIT_CONFIGURATION);

        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");

        PitPublisher publisher = new PitPublisher();
        publisher.setMutationStatsFile("**/mutations.xml");
        project.getPublishersList().add(publisher);
        project.getPublishersList().add(new AutoGrader(PIT_CONFIGURATION));

        Run<?, ?> freestyle = buildSuccessfully(project);
        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(pipeline, 87);
        assertAchievedScore(pipeline, 87);
        assertPitMuatation(freestyle);
        assertPitMuatation(pipeline);
    }

    private void assertPitMuatation(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);
        PitConfiguration pitConfiguration = new PitConfiguration.PitConfigurationBuilder()
                .setDetectedImpact(1)
                .setUndetectedImpact(-1)
                .setMaxScore(100)
                .build();

        assertThat(score).hasPitConfiguration(pitConfiguration);

        PitScore pitScore = score.getPitScores().get(0);
        assertThat(pitScore).hasMutationsSize(191);
        assertThat(pitScore).hasDetectedSize(139);
        assertThat(pitScore).hasUndetectedSize(52);
        assertThat(pitScore).hasUndetectedPercentage(27);
        assertThat(pitScore).hasDetectedPercentage(73);
    }

    private AggregatedScore getAggregatedScore(final Run<?, ?> baseline) {
        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        return actions.get(0).getResult();
    }

    private void assertAchievedScore(final Run<?, ?> pipeLineJob, final int scoreToBeAsserted) {
        List<AutoGradingBuildAction> actions = pipeLineJob.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);

        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(scoreToBeAsserted);
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
