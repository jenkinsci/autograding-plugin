package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.jvnet.hudson.test.JenkinsRule;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;

import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.Pit;
import io.jenkins.plugins.analysis.warnings.Pmd;
import io.jenkins.plugins.analysis.warnings.SpotBugs;
import io.jenkins.plugins.analysis.warnings.checkstyle.CheckStyle;
import io.jenkins.plugins.grading.AnalysisConfiguration.AnalysisConfigurationBuilder;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 * @author Oliver Scholz
 *
 */
public class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {

    private final String ANALYSIS_CONFIGURATION = "{\"analysis\": {" +
            "\"maxScore\": 100," +
            "\"errorImpact\": -10," +
            "\"highImpact\": -5," +
            "\"normalImpact\": -2," +
            "\"lowImpact\": -1}}";

    private final String PIT_CONFIGURATION = "{\"pit\": {"
            + "\"maxScore\": 100,"
            + "\"detectedImpact\": 1,"
            + "\"undetectedImpact\": -1,"
            + "\"ratioImpact\": 0"
            + "}}";

    private final String COVERAGE_CONFIGURATION = "{\"coverage\": {"
            + "\"maxScore\": 100,"
            + "\"coveredImpact\": 1,"
            + "\"missedImpact\": -1"
            + "}}";

    /** Verifies that the step skips all autograding parts if the configuration is empty. */
    @Test
    public void shouldSkipGradingIfConfigurationIsEmptyFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");
        IssuesRecorder recorder = new IssuesRecorder();
        final CheckStyle checkstyle = new CheckStyle();
        checkstyle.setPattern("**/checkstyle.xml");
        recorder.setTools(checkstyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader("{}"));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Skipping static analysis results");
        assertThat(getConsoleLog(run)).contains("[Autograding] Skipping test results");
        assertThat(getConsoleLog(run)).contains("[Autograding] Skipping coverage results");
        assertThat(getConsoleLog(run)).contains("[Autograding] Skipping mutation coverage results");
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if testing has been requested, but no testing action has been recorded.
     */
    @Test
    public void shouldAbortBuildSinceNoTestActionHasBeenRegisteredFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        final String TEST_CONFIGURATION = "{\"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-5,\"skippedImpact\":-1}}";
        project.getPublishersList().add(new AutoGrader(TEST_CONFIGURATION));

        Run<?, ?> baseline = buildWithResult(project, Result.FAILURE);

        assertThat(getConsoleLog(baseline)).contains("java.lang.IllegalArgumentException: Test scoring has been enabled, but no test results have been found.");
    }

    /**
     * Verifies that CheckStyle results are correctly graded.
     */
    @Test
    public void shouldCountCheckStyleWarningsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");
        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkstyle = new CheckStyle();
        checkstyle.setPattern("**/checkstyle.xml");
        recorder.setTools(checkstyle);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Grading static analysis results for CheckStyle");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score -60 (warnings distribution err:6, high:0, normal:0, low:0)");
        assertThat(getConsoleLog(run)).contains("[Autograding] Total score for static analysis results: 40");

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        assertThat(score).hasAchieved(40);
    }

    @Ignore("Coverage Recorder?")
    @Test
    public void shouldCountJacocoWarningFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");
        IssuesRecorder recorder = new IssuesRecorder();


        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(COVERAGE_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Grading coverage results Coverage Report");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score -10 - from recorded line coverage results: 45%");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score 28 - from recorded branch coverage results: 64%");
        assertThat(getConsoleLog(run)).contains("[Autograding] Total score for coverage results: 18");

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        CoverageConfiguration covConfiguration = new CoverageConfiguration.CoverageConfigurationBuilder()
                .setMaxScore(100)
                .setCoveredImpact(1)
                .setMissedImpact(-1)
                .build();

        // Check if CoverageConfiguration equals an identically setup configuration object
        assertThat(score).hasCoverageConfiguration(covConfiguration);
        assertThat(score).hasTotal(100);
        assertThat(score).hasCoverageAchieved(18);
        assertThat(score).hasAchieved(18);
    }

    @Test
    public void shouldCountPmdWarningsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("pmd.xml");
        IssuesRecorder recorder = new IssuesRecorder();
        final Pmd pmd = new Pmd();
        pmd.setPattern("**/pmd.xml");
        recorder.setTools(pmd);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Grading static analysis results for PMD");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score -8 (warnings distribution err:0, high:1, normal:1, low:1)");
        assertThat(getConsoleLog(run)).contains("[Autograding] Total score for static analysis results: 92 of 100");

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setMaxScore(100)
                .setErrorImpact(-10)
                .setHighImpact(-5)
                .setNormalImpact(-2)
                .setLowImpact(-1)
                .build();

        assertThat(score).hasAnalysisConfiguration(analysisConfiguration);
        assertThat(score).hasTotal(100);
        assertThat(score).hasAchieved(92);
    }

    @Test
    public void shoudCountSpotbugsFindingsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("spotbugsXml.xml");
        IssuesRecorder recorder = new IssuesRecorder();
        SpotBugs spotBugs = new SpotBugs();
        spotBugs.setPattern("**/spotbugsXml.xml");
        recorder.setTools(spotBugs);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(ANALYSIS_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);

        assertThat(getConsoleLog(run)).contains("[Autograding] Grading static analysis results for SpotBugs");
        assertThat(getConsoleLog(run)).contains("[Autograding] -> Score -17 (warnings distribution err:0, high:2, normal:2, low:3)");
        assertThat(getConsoleLog(run)).contains("[Autograding] Total score for static analysis results: 83 of 100");

        List<AutoGradingBuildAction> actions = run.getActions(AutoGradingBuildAction.class);
        assertThat(actions).hasSize(1);
        AggregatedScore score = actions.get(0).getResult();

        AnalysisConfiguration analysisConfiguration = new AnalysisConfigurationBuilder()
                .setMaxScore(100)
                .setErrorImpact(-10)
                .setHighImpact(-5)
                .setNormalImpact(-2)
                .setLowImpact(-1)
                .build();

        assertThat(score).hasAnalysisConfiguration(analysisConfiguration);
        assertThat(score).hasTotal(100);
        assertThat(score).hasAchieved(83);
    }

    @Disabled
    @Test
    public void shouldCountPitWarningsFreestyle() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");
        IssuesRecorder recorder = new IssuesRecorder();
        Pit pit = new Pit();
        pit.setPattern("**/mutations.xml");
        recorder.setTools(pit);

        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(PIT_CONFIGURATION));

        Run<?, ?> run = buildSuccessfully(project);
        //assertThat(getConsoleLog(baseline)).contains("[Autograding] Grading coverage results Coverage Report");
        //assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score -10 - from recorded line coverage results: 45%");
        //assertThat(getConsoleLog(baseline)).contains("[Autograding] -> Score 28 - from recorded branch coverage results: 64%");
        //assertThat(getConsoleLog(baseline)).contains("[Autograding] Total score for coverage results: 18");

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

    private void configureScanner(final WorkflowJob job, final String tool, final String fileName, final String configuration) {
        job.setDefinition(new CpsFlowDefinition("node {\n"
                + "  stage ('Integration Test') {\n"
                + "         recordIssues tool: " + tool + "(pattern: '**/" + fileName + "*')\n"
                + "         autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}", true));
    }

    private void configureJacocoScanner(final WorkflowJob job, final String fileName, final String configuration) {
        job.setDefinition(new CpsFlowDefinition("node {\n"
                + "  stage ('Integration Test') {\n"
                + "     publishCoverage adapters: [jacocoAdapter('**/" + fileName + "*')]\n"
                + "     autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}", true));
    }

    private void configurePitScanner(final WorkflowJob job, final String fileName, final String configuration) {
        job.setDefinition(new CpsFlowDefinition("node {\n"
                + "  stage ('Mutation Coverage') {\n"
                + "     step([$class: 'PitPublisher', mutationStatsFile: '**/" + fileName + "*'])\n"
                + "     autoGrade('" + configuration + "')\n"
                + "  }\n"
                + "}", true));
    }

}
