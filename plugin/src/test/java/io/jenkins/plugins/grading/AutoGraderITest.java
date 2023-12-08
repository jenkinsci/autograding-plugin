package io.jenkins.plugins.grading;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.TestScore;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.tasks.Recorder;
import hudson.tasks.junit.JUnitResultArchiver;

import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.warnings.CheckStyle;
import io.jenkins.plugins.analysis.warnings.Cpd;
import io.jenkins.plugins.analysis.warnings.Pmd;
import io.jenkins.plugins.analysis.warnings.SpotBugs;
import io.jenkins.plugins.coverage.metrics.steps.CoverageRecorder;
import io.jenkins.plugins.coverage.metrics.steps.CoverageTool;
import io.jenkins.plugins.coverage.metrics.steps.CoverageTool.Parser;
import io.jenkins.plugins.util.IntegrationTestWithJenkinsPerSuite;

import static edu.hm.hafner.grading.assertions.Assertions.*;

/**
 * Integration tests for the {@link AutoGrader} step.
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
class AutoGraderITest extends IntegrationTestWithJenkinsPerSuite {
    private static final String TEST_CONFIGURATION = """
              "tests": {
                "tools": [
                    {
                      "id": "tests",
                      "name": "Tests",
                      "pattern": "target/tests.xml"
                    }
                  ],
                "passedImpact": 1,
                "failureImpact": -5,
                "skippedImpact": -1,
                "maxScore": 100
              }
            """;
    private static final String ANALYSIS_CONFIGURATION = """
              "analysis": {
                "id": "checkstyle",
                "tools": [
                    {
                      "id": "checkstyle",
                      "name": "CheckStyle"
                    }
                  ],
                "errorImpact": -10,
                "highImpact": -5,
                "normalImpact": -2,
                "lowImpact": -1,
                "maxScore": 100
              }
            """;
    private static final String ANALYSIS_MULTI_CONFIGURATION = """
                  "analysis": [{
                    "tools": [
                        {
                          "id": "pmd",
                          "name": "PMD"
                        },
                        {
                          "id": "cpd",
                          "name": "CPD"
                        }
                      ],
                    "name": "Style",
                    "errorImpact": -10,
                    "highImpact": -5,
                    "normalImpact": -2,
                    "lowImpact": -1,
                    "maxScore": 100
                  },
                  {
                    "tools": [
                        {
                          "id": "spotbugs",
                          "name": "SpotBugs"
                        }
                      ],
                    "name": "Bugs",
                    "errorImpact": -10,
                    "highImpact": -5,
                    "normalImpact": -2,
                    "lowImpact": -1,
                    "maxScore": 100
                  }
                  ]
                  """;
    private static final String COVERAGE_CONFIGURATION = """
              "coverage": {
                "tools": [
                  {
                    "id": "coverage",
                    "metric": "line",
                    "pattern": "target/jacoco.xml"
                  }
                ],
                "maxScore": 100,
                "coveredPercentageImpact": 1,
                "missedPercentageImpact": -1
              }
            """;
    private static final String MUTATION_CONFIGURATION = """
              "coverage": {
                "tools": [
                  {
                    "id": "pit",
                    "metric": "mutation",
                    "pattern": "target/pit.xml"
                  }
                ],
                "maxScore": 100,
                "coveredPercentageImpact": 1,
                "missedPercentageImpact": -1
              }
            """;
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

    @Test
    void shouldSkipGradingIfConfigurationIsEmpty() {
        WorkflowJob job = createPipeline();

        configureScanner(job, "checkstyle", "");
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Processing 0 static analysis configuration(s)",
                "[Autograding] Processing 0 coverage configuration(s)",
                "[Autograding] Processing 0 test configuration(s)");
    }

    @Test
    void shouldAbortBuildWhenNoTestActionHasBeenRegistered() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");

        configureScanner(job, "checkstyle", TEST_CONFIGURATION);
        Run<?, ?> baseline = buildSuccessfully(job);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Reading configuration:",
                "[Autograding] Processing 0 static analysis configuration(s)",
                "[Autograding] Processing 0 coverage configuration(s)",
                "[Autograding] Grading test results",
                "[Autograding] Processing 1 test configuration(s)",
                "[Autograding] [-ERROR-] Scoring of test results has been enabled, but no results have been found.");
        assertThat(getConsoleLog(baseline)).containsIgnoringWhitespaces(TEST_CONFIGURATION);
    }

    @Test
    void shouldCountCheckStyleWarningsInFreestyleJob() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("checkstyle.xml");

        IssuesRecorder recorder = new IssuesRecorder();
        CheckStyle checkStyle = new CheckStyle();
        checkStyle.setPattern("**/*checkstyle.xml");
        recorder.setTools(checkStyle);

        addAutoGrader(project, recorder, ANALYSIS_CONFIGURATION);

        Run<?, ?> freestyle = buildSuccessfully(project);

        assertAchievedScore(freestyle, 40);
        assertCheckStyleScore(freestyle);
    }

    @Test
    void shouldCountCheckStyleWarningsInPipeline() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("checkstyle.xml");
        configureScanner(job, "checkstyle", ANALYSIS_CONFIGURATION);

        Run<?, ?> pipeline = buildSuccessfully(job);
        assertAchievedScore(pipeline, 40);
        assertCheckStyleScore(pipeline);
    }

    private void assertCheckStyleScore(final Run<?, ?> build) {
        AggregatedScore aggregation = getAggregatedScore(build);
        assertThat(aggregation).hasAnalysisAchievedScore(40);

        assertThat(aggregation.getAnalysisScores().get(0))
                .hasId("checkstyle")
                .hasErrorSize(6)
                .hasHighSeveritySize(0)
                .hasNormalSeveritySize(0)
                .hasLowSeveritySize(0)
                .hasTotalSize(6)
                .hasImpact(-60);

        assertThat(getConsoleLog(build)).containsIgnoringWhitespaces(ANALYSIS_CONFIGURATION);
        assertThat(getConsoleLog(build)).contains(
                "[Autograding] Grading static analysis results",
                "[Autograding] Processing 1 static analysis configuration(s)",
                "[Autograding] Processing 0 coverage configuration(s)",
                "[Autograding] Processing 0 test configuration(s)",
                "[Autograding] -> Found result action for CheckStyle Warnings with 6 issues",
                "[Autograding] => Static Analysis Warnings Score: 40 of 100");
    }

    @Test
    void shouldGradeMultipleAnalysisResultsInFreestyleJob() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(ANALYSIS_REPORTS);
        IssuesRecorder recorder = new IssuesRecorder();

        Pmd pmd = new Pmd();
        Cpd cpd = new Cpd();
        SpotBugs spotBugs = new SpotBugs();
        recorder.setTools(pmd, cpd, spotBugs);

        addAutoGrader(project, recorder, ANALYSIS_MULTI_CONFIGURATION);

        Run<?, ?> freestyle = buildSuccessfully(project);

        assertAchievedScore(freestyle, 185);
        assertMultipleAnalysisScores(freestyle);
    }

    @Test
    void shouldGradeMultipleAnalysisResultsInPipeline() {
        WorkflowJob pipelineJob = createPipelineWithWorkspaceFiles(ANALYSIS_REPORTS);
        configureScanner(pipelineJob, "recordIssues", ANALYSIS_MULTI_CONFIGURATION);

        Run<?, ?> pipeline = buildSuccessfully(pipelineJob);

        assertAchievedScore(pipeline, 185);
        assertMultipleAnalysisScores(pipeline);
    }

    private void assertMultipleAnalysisScores(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Grading static analysis results",
                "[Autograding] Processing 2 static analysis configuration(s)",
                "[Autograding] -> Found result action for PMD Warnings with 4 issues",
                "[Autograding] -> Found result action for CPD Duplications with 7 issues",
                "[Autograding] => Style Score: 85 of 100",
                "[Autograding] -> Found result action for SpotBugs Warnings with 0 issues",
                "[Autograding] => Bugs Score: 100 of 100");
        assertThat(getConsoleLog(baseline)).containsIgnoringWhitespaces(ANALYSIS_MULTI_CONFIGURATION);

        assertThat(score.getAnalysisScores()).hasSize(2);

        var analysisScores = score.getAnalysisScores().get(0).getSubScores();
        assertThat(analysisScores).hasSize(2);

        assertThat(analysisScores.get(0)).hasId("pmd")
                .hasErrorSize(0)
                .hasHighSeveritySize(0)
                .hasNormalSeveritySize(4)
                .hasLowSeveritySize(0)
                .hasTotalSize(4)
                .hasImpact(-8);

        assertThat(analysisScores.get(1)).hasId("cpd")
                .hasErrorSize(0)
                .hasHighSeveritySize(0)
                .hasNormalSeveritySize(0)
                .hasLowSeveritySize(7)
                .hasTotalSize(7)
                .hasImpact(-7);

        analysisScores = score.getAnalysisScores().get(1).getSubScores();
        assertThat(analysisScores).hasSize(1);

        assertThat(analysisScores.get(0)).hasId("spotbugs")
                .hasErrorSize(0)
                .hasHighSeveritySize(0)
                .hasNormalSeveritySize(0)
                .hasLowSeveritySize(0)
                .hasTotalSize(0)
                .hasImpact(0);
    }

    @Test
    void shouldGradeCoverageInFreestyleJob() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml");

        CoverageRecorder coveragePublisher = new CoverageRecorder();
        var tool = new CoverageTool();
        tool.setParser(Parser.JACOCO);
        coveragePublisher.setTools(List.of(tool));

        addAutoGrader(project, coveragePublisher, COVERAGE_CONFIGURATION);

        Run<?, ?> freestyle = buildSuccessfully(project);

        assertAchievedScore(freestyle, 76);
        assertCoverageScore(freestyle);
    }

    @Test
    void shouldGradeCoverageInPipeline() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("jacoco.xml");
        configureScanner(job, "jacoco", COVERAGE_CONFIGURATION);

        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(pipeline, 76);
        assertCoverageScore(pipeline);
    }

    private void assertCoverageScore(final Run<?, ?> baseline) {
        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Processing 1 coverage configuration(s)",
                "[Autograding] -> Found result action for Coverage Report: [MODULE] Autograding Plugin",
                "[Autograding] => Code Coverage Score: 76 of 100");
        assertThat(getConsoleLog(baseline)).containsIgnoringWhitespaces(COVERAGE_CONFIGURATION);

        AggregatedScore score = getAggregatedScore(baseline);
        assertThat(score).hasCoverageAchievedScore(76);
        assertThat(score.getCoverageScores().get(0))
                .hasId("coverage")
                .hasCoveredPercentage(88)
                .hasMissedPercentage(12)
                .hasImpact(76);
    }

    @Test
    void shouldGradeMutationCoverageInFreestyleJob() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("mutations.xml");

        CoverageRecorder coveragePublisher = new CoverageRecorder();
        coveragePublisher.setId("pit");
        var tool = new CoverageTool();
        tool.setParser(Parser.PIT);
        coveragePublisher.setTools(List.of(tool));

        addAutoGrader(project, coveragePublisher, MUTATION_CONFIGURATION);

        Run<?, ?> freestyle = buildSuccessfully(project);

        assertMutationScore(freestyle);
        assertAchievedScore(freestyle, 46);
    }

    @Test
    void shouldGradeMutationCoverageInPipeline() {
        WorkflowJob job = createPipelineWithWorkspaceFiles("mutations.xml");
        configureScanner(job, "mutations", MUTATION_CONFIGURATION);

        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(pipeline, 46);
        assertMutationScore(pipeline);
    }

    private void assertMutationScore(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);
        assertThat(score).hasCoverageAchievedScore(46);
        assertThat(score.getCoverageScores().get(0))
                .hasId("coverage")
                .hasCoveredPercentage(73)
                .hasMissedPercentage(27)
                .hasImpact(46);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Processing 1 coverage configuration(s)",
                "[Autograding] -> Found result action for Coverage Report: [MODULE] - <1> []",
                "[Autograding] => Code Coverage Score: 46 of 100"
        );
    }

    @Test
    void shouldGradeMultipleTestResultsInFreestyleJob() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles(TEST_REPORTS);
        JUnitResultArchiver jUnitResultArchiver = new JUnitResultArchiver("*");
        project.getPublishersList().add(jUnitResultArchiver);
        project.getPublishersList().add(new AutoGrader(json(TEST_CONFIGURATION)));

        Run<?, ?> freestyle = buildSuccessfully(project);

        assertAchievedScore(freestyle, 61);
        assertTestScore(freestyle);
    }

    private String json(final String value) {
        return String.format("{%s}", value);
    }

    @Test
    void shouldGradeMultipleTestResultsInPipelines() {
        WorkflowJob job = createPipelineWithWorkspaceFiles(TEST_REPORTS);
        configureScanner(job, "junit", TEST_CONFIGURATION);

        Run<?, ?> pipeline = buildSuccessfully(job);

        assertAchievedScore(pipeline, 61);
        assertTestScore(pipeline);
    }

    private void assertTestScore(final Run<?, ?> baseline) {
        AggregatedScore score = getAggregatedScore(baseline);

        assertThat(getConsoleLog(baseline)).contains(
                "[Autograding] Grading static analysis results",
                "[Autograding] Processing 1 test configuration(s)",
                "[Autograding] -> Found result action for tests: Test Result",
                "[Autograding] => Tests Score: 61 of 100"
        );
        assertThat(getConsoleLog(baseline)).containsIgnoringWhitespaces(TEST_CONFIGURATION);

        TestScore testScore = score.getTestScores().get(0);
        assertThat(testScore).hasPassedSize(61)
                .hasTotalSize(61)
                .hasFailedSize(0)
                .hasSkippedSize(0)
                .hasImpact(61);
    }

    @Test
    void shouldGradeEverything() {
        FreeStyleProject project = createFreeStyleProjectWithWorkspaceFiles("jacoco.xml", "mutations.xml",
                "pmd.xml", "cpd.xml", "spotbugsXml.xml", "TEST-InjectedTest.xml",
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

        CoverageRecorder coveragePublisher = new CoverageRecorder();
        var jacoco = new CoverageTool();
        jacoco.setParser(Parser.JACOCO);
        coveragePublisher.setTools(List.of(jacoco));
        project.getPublishersList().add(coveragePublisher);

        CoverageRecorder mutationRecorder = new CoverageRecorder();
        mutationRecorder.setId("pit");
        mutationRecorder.setName("Mutation Report");
        var tool = new CoverageTool();
        tool.setParser(Parser.PIT);
        mutationRecorder.setTools(List.of(tool));
        project.getPublishersList().add(mutationRecorder);

        Pmd pmd = new Pmd();
        Cpd cpd = new Cpd();
        SpotBugs spotBugs = new SpotBugs();
        IssuesRecorder recorder = new IssuesRecorder();
        recorder.setTools(pmd, cpd, spotBugs);

        addAutoGrader(project, recorder, """
                  "tests": {
                    "tools": [
                        {
                          "id": "tests",
                          "name": "Tests",
                          "pattern": "target/tests.xml"
                        }
                      ],
                    "passedImpact": 1,
                    "failureImpact": -5,
                    "skippedImpact": -1,
                    "maxScore": 100
                  },
                  "analysis": [
                  {
                    "tools": [
                        {
                          "id": "pmd",
                          "name": "PMD"
                        },
                        {
                          "id": "cpd",
                          "name": "CPD"
                        }
                      ],
                    "name": "Style",
                    "errorImpact": -10,
                    "highImpact": -5,
                    "normalImpact": -2,
                    "lowImpact": -1,
                    "maxScore": 100
                  },
                  {
                    "tools": [
                        {
                          "id": "spotbugs",
                          "name": "SpotBugs"
                        }
                      ],
                    "name": "Bugs",
                    "errorImpact": -10,
                    "highImpact": -5,
                    "normalImpact": -2,
                    "lowImpact": -1,
                    "maxScore": 100
                  }
                  ],
                  "coverage": [
                  {
                      "tools": [
                          {
                            "metric": "line",
                            "name" : "Line Coverage",
                            "id": "coverage"
                          },
                          {
                            "metric": "branch",
                            "name" : "Branch Coverage",
                            "id": "coverage"
                          }
                        ],
                    "name": "Code Coverage",
                    "maxScore": 50,
                    "coveredPercentageImpact": 1,
                    "missedPercentageImpact": -1
                  },
                  {
                      "tools": [
                          {
                            "metric": "mutation",
                            "name" : "PIT Mutation Coverage",
                            "id": "pit"
                          }
                        ],
                    "name": "Mutation Coverage",
                    "id": "mutation",
                    "maxScore": 50,
                    "coveredPercentageImpact": 1,
                    "missedPercentageImpact": -1
                  }
                  ]
                """);

        Run<?, ?> build = buildSuccessfully(project);

        assertAchievedScore(build, 342);
        assertTestScore(build);
        assertMultipleAnalysisScores(build);

        assertThat(getConsoleLog(build)).contains(
                "[Autograding] Processing 2 coverage configuration(s)",
                "[Autograding] -> Found result action for Coverage Report: [MODULE] Autograding Plugin",
                "[Autograding] => Code Coverage Score: 50 of 50",
                "[Autograding] -> Found result action for Mutation Report: [MODULE] - <1> []",
                "[Autograding] => Mutation Coverage Score: 46 of 50");

        AggregatedScore score = getAggregatedScore(build);
        assertThat(score).hasCoverageAchievedScore(96);
        assertThat(score.getCoverageScores()).hasSize(2);
        assertThat(score.getCoverageScores().get(0))
                .hasId("coverage")
                .hasCoveredPercentage(75)
                .hasMissedPercentage(25)
                .hasImpact(50);
        assertThat(score.getCoverageScores().get(1))
                .hasId("mutation")
                .hasCoveredPercentage(73)
                .hasMissedPercentage(27)
                .hasImpact(46);
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
        assertThat(score).hasAchievedScore(scoreToBeAsserted);
    }

    /**
     * Returns the console log as a String.
     *
     * @param build
     *         the build to get the log for
     *
     * @return the console log
     */
    @Override
    protected String getConsoleLog(final Run<?, ?> build) {
        try {
            return JenkinsRule.getLog(build);
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void configureScanner(final WorkflowJob job, final String stepName, final String configuration) {
        var pipelineScript = new StringBuilder(1024);

        pipelineScript.append("""
                node {
                  stage ('Integration Test') {
                """);

        switch (stepName) {
            case "mutations":
                pipelineScript.append("         recordCoverage tools: [[parser: 'PIT']], id: 'pit'\n");
                break;
            case "jacoco":
                pipelineScript.append("         recordCoverage tools: [[parser: 'JACOCO']]\n");
                break;
            case "checkstyle":
                pipelineScript.append("         recordIssues tool: checkStyle(pattern: '**/checkstyle*')\n");
                break;
            case "recordIssues":
                pipelineScript.append("         recordIssues tools: ["
                        + "spotBugs(pattern: '**/spotbugsXml.xml'),\n");
                pipelineScript.append("                 "
                        + "pmdParser(pattern: '**/pmd.xml'),\n");
                pipelineScript.append("                 "
                        + "cpd(pattern: '**/cpd.xml')]\n");
                break;
            case "junit":
                pipelineScript.append("         junit testResults: '**/TEST-*.xml'\n");
                break;
            default:
                break;

        }
        pipelineScript
                .append("         autoGrade('{")
                .append(StringUtils.deleteWhitespace(configuration))
                .append("}')\n");
        pipelineScript.append("  }\n"
                + "}");
        job.setDefinition(new CpsFlowDefinition(pipelineScript.toString(), true));
    }

    private void addAutoGrader(final FreeStyleProject project,
            final Recorder recorder, final String configuration) {
        project.getPublishersList().add(recorder);
        project.getPublishersList().add(new AutoGrader(json(configuration)));
    }
}
