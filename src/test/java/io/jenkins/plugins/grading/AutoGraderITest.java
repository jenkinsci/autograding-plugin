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
import io.jenkins.plugins.analysis.warnings.Pmd;
import io.jenkins.plugins.analysis.warnings.checkstyle.CheckStyle;
import io.jenkins.plugins.coverage.CoveragePublisher;
import io.jenkins.plugins.coverage.adapter.CoverageAdapter;
import io.jenkins.plugins.coverage.adapter.JacocoReportAdapter;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 * @author Thomas Großbeck
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {
    private static final String ANALYSIS_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String CHECKSTYLE_CONFIGURATON = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}";
    private static final String TEST_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
    private static final String COVERAGE_CONFIGURATION = "{\"coverage\":{\"maxScore\":100,\"coveredImpact\":1,\"missedImpact\":-1}}";
    private static final String PIT_CONFIGURATION = "{\"pit\":{\"maxScore\":100,\"detectedImpact\":1,\"undetectedImpact\":-1,\"ratioImpact\":0}}";
    private static final String FULL_CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}, \"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}, \"coverage\":{\"maxScore\":100,\"coveredImpact\":1,\"missedImpact\":-1}, \"pit\":{\"maxScore\":100,\"detectedImpact\":1,\"undetectedImpact\":-1,\"ratioImpact\":0}}";

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

        configureScanner(job, "checkstyle", CHECKSTYLE_CONFIGURATON);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertCheckStyleWarnings(baseline);
    }

    @Test
    public void shouldCountCheckStyleWarningsFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        recorder.setTools(checkStyle);

        job.getPublishersList().add(recorder);
        job.getPublishersList().add(new AutoGrader(CHECKSTYLE_CONFIGURATON));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertCheckStyleWarnings(baseline);
    }

    public void assertCheckStyleWarnings(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for static analysis results: 40");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(40);
    }

    @Test
    public void shouldCalculateStaticAnalysisResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("pmd.xml");

        configureScanner(job, "pmd", ANALYSIS_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertStaticAnalysis(baseline);
    }

    @Test
    public void shouldCalculateStaticAnalysisFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("pmd.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        recorder.setTools(new Pmd());

        job.getPublishersList().add(recorder);
        job.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertStaticAnalysis(baseline);
    }

    public void assertStaticAnalysis(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for PMD");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(96);
    }

    @Test
    public void shouldCalculateTestResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        configureScanner(job, "test", TEST_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertTestResults(baseline);
    }

    @Test
    public void shouldCalculateTestFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        JUnitResultArchiver resultArchiver = new JUnitResultArchiver("**/TEST-*.xml");

        job.getPublishersList().add(resultArchiver);
        job.getPublishersList().add(new AutoGrader(TEST_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertTestResults(baseline);
    }

    public void assertTestResults(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results Test Result");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 46 - from recorded test results: 46, 46, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(46);
    }

    @Test
    public void shouldCalculateCoverageResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");

        configureScanner(job, "jacoco", COVERAGE_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertCoverage(baseline);
    }

    @Test
    public void shouldCalculateCoverageFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        JacocoReportAdapter adapter = new JacocoReportAdapter("**/jacoco.xml");
        List<CoverageAdapter> adapters = new ArrayList<CoverageAdapter>() {{add(adapter);}};

        CoveragePublisher recorder = new CoveragePublisher();
        recorder.setAdapters(adapters);

        job.getPublishersList().add(recorder);
        job.getPublishersList().add(new AutoGrader(COVERAGE_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertCoverage(baseline);
    }

    public void assertCoverage(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results Coverage Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 64 - from recorded line coverage results: 82%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -8 - from recorded branch coverage results: 46%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping mutation coverage results");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(56);
    }

    @Test
    public void shouldCalculatePitResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");

        configureScanner(job, "mutations", PIT_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertPit(baseline);
    }

    @Test
    public void shouldCalculatePitFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");

        PitPublisher publisher = new PitPublisher("**/mutations.xml", 0, false);

        job.getPublishersList().add(publisher);
        job.getPublishersList().add(new AutoGrader(PIT_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertPit(baseline);
    }

    public void assertPit(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 87 - from recorded PIT mutation results: 191, 52, 139, 28");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(87);
    }

    @Test
    public void shouldCalculateFullResults() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("pmd.xml", "jacoco.xml", "mutations.xml",
                "TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        configureScannerForFullGrading(job, FULL_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertFullResults(baseline);
    }

    @Test
    public void shouldCalculateFullResultsFreestyle() {
        FreeStyleProject job = createFreeStyleProjectWithWorkspaceFiles("pmd.xml", "jacoco.xml", "mutations.xml",
                "TEST-io.jenkins.plugins.grading.AggregatedScoreXmlStreamTest.xml",
                "TEST-io.jenkins.plugins.grading.AnalysisScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ArchitectureRulesTest.xml",
                "TEST-io.jenkins.plugins.grading.AutoGraderTest.xml",
                "TEST-io.jenkins.plugins.grading.CoverageScoreTests.xml",
                "TEST-io.jenkins.plugins.grading.PackageArchitectureTest.xml",
                "TEST-io.jenkins.plugins.grading.PitScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.ScoreTest.xml",
                "TEST-io.jenkins.plugins.grading.TestScoreTest.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        recorder.setTools(new Pmd());
        job.getPublishersList().add(recorder);

        JUnitResultArchiver resultArchiver = new JUnitResultArchiver("**/TEST-*.xml");
        job.getPublishersList().add(resultArchiver);

        JacocoReportAdapter adapter = new JacocoReportAdapter("**/jacoco.xml");
        List<CoverageAdapter> adapters = new ArrayList<CoverageAdapter>() {{add(adapter);}};
        CoveragePublisher coveragePublisher = new CoveragePublisher();
        coveragePublisher.setAdapters(adapters);
        job.getPublishersList().add(coveragePublisher);

        PitPublisher pitPublisher = new PitPublisher("**/mutations.xml", 0, false);
        job.getPublishersList().add(pitPublisher);

        job.getPublishersList().add(new AutoGrader(FULL_CONFIGURATION));

        Run<?, ?> baseline = buildSuccessfully(job);

        assertFullResults(baseline);
    }

    public void assertFullResults(Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading static analysis results for PMD");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading test results Test Result");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 46 - from recorded test results: 46, 46, 0, 0");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results Coverage Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 64 - from recorded line coverage results: 82%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -8 - from recorded branch coverage results: 46%");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading PIT mutation results PIT Mutation Report");
        assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 87 - from recorded PIT mutation results: 191, 52, 139, 28");

        List<AutoGradingBuildAction> actions = baseline.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();
        assertThat(score).hasAchieved(285);
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

    private void configureScannerForFullGrading(final WorkflowJob job, final String configuration) {
        job.setDefinition(new CpsFlowDefinition("node {\n"
                + "  stage ('Integration Test') {\n"
                + "         recordIssues tool: pmdParser(pattern: '**/pmd.xml')\n"
                + "         junit testResults: '**/TEST-*.xml'\n"
                + "         publishCoverage adapters: [jacocoAdapter('**/jacoco.xml')]\n"
                + "         step([$class: 'PitPublisher', mutationStatsFile: '**/mutations.xml'])\n"
                + "         autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}", true));
    }

    private void configureScanner(final WorkflowJob job, final String fileName, final String configuration) {
        String definition = "node {\n"
                + "  stage ('Integration Test') {\n";
        switch(fileName) {
            case "checkstyle":
                definition += "         recordIssues tool: checkStyle(pattern: '**/" + fileName + "*')\n";
                break;
            case "pmd":
                definition += "         recordIssues tool: pmdParser(pattern: '**/" + fileName + "*')\n";
                break;
            case "test":
                definition += "         junit testResults: '**/TEST-*.xml'\n";
                break;
            case "jacoco":
                definition += "         publishCoverage adapters: [jacocoAdapter('**/" + fileName + "*')]\n";
                break;
            case "mutations":
                definition += "         step([$class: 'PitPublisher', mutationStatsFile: '**/" + fileName + "*'])\n";
                break;
            default:
                break;
        }
        definition += "         autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(definition, true));
    }

}
