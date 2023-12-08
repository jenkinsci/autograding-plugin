package io.jenkins.plugins.grading;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import org.jenkinsci.test.acceptance.junit.AbstractJUnitTest;
import org.jenkinsci.test.acceptance.junit.WithPlugins;
import org.jenkinsci.test.acceptance.po.Build;
import org.jenkinsci.test.acceptance.po.Job;
import org.jenkinsci.test.acceptance.po.WorkflowJob;

import static org.assertj.core.api.Assertions.*;

/**
 * Acceptance tests for the AutoGrading Plugin.
 *
 * @author Lukas Kirner
 */
@WithPlugins({"autograding", "warnings-ng", "junit", "coverage", "pipeline-stage-step", "workflow-durable-task-step", "workflow-basic-steps"})
public class SmokeTests extends AbstractJUnitTest {
    private static final String AUTOGRADING_PLUGIN_PREFIX = "/autograding_test/";
    private static final String CONFIGURATION = """
                {
                  "tests": [{
                    "tools": [
                      {
                        "id": "tests"
                      }
                    ],
                    "id": "tests",
                    "name": "JUnit",
                    "skippedImpact": -1,
                    "failureImpact": -5,
                    "maxScore": 100
                  }],
                  "analysis": [
                    {
                      "id": "analysis",
                      "tools": [
                        {
                          "id": "checkstyle",
                          "name": "CheckStyle"
                        },
                        {
                          "id": "pmd",
                          "name": "PMD"
                        },
                        {
                          "id": "cpd",
                          "name": "CPD"
                        },
                        {
                          "id": "findbugs",
                          "name": "FindBugs"
                        }
                      ],
                      "errorImpact": 1,
                      "highImpact": 2,
                      "normalImpact": 3,
                      "lowImpact": 4,
                      "maxScore": 100
                    }
                  ],
                  "coverage": [
                  {
                    "tools": [
                      {
                        "id": "coverage",
                        "name": "Line Coverage",
                        "metric": "line"
                      },
                      {
                        "id": "coverage",
                        "name": "Branch Coverage",
                        "metric": "branch"
                      }
                    ],
                    "id": "coverage",
                    "name": "Line Coverage",
                    "maxScore": 100,
                    "missedPercentageImpact": -1
                  },
                  {
                    "tools": [
                      {
                        "id": "pit",
                        "name": "Mutation Coverage",
                        "metric": "mutation"
                      }
                    ],
                    "id": "mutation",
                    "name": "Mutation Coverage",
                    "maxScore": 100,
                    "missedPercentageImpact": -1
                  }
                  ]
                }
                """;

    /**
     * Test all cards with all tools.
     */
    @Test
    public void testWithAllCards() {
        WorkflowJob job = jenkins.jobs.create(WorkflowJob.class);
        job.sandbox.check();

        configurePipeline(job, CONFIGURATION, "checkstyle-result.xml", "pmd.xml", "cpd.xml", "Main.java", "jacoco.xml", "mutations.xml", "TEST-TestScore.xml");

        job.save();
        Build build = shouldBuildJobUnstable(job); // unstable due to failing tests

        AutoGradePageObject pageObject = new AutoGradePageObject(build, buildAutoGradeURLFromJob(job));

        assertThat(pageObject.getTotalScoreInPercent()).isEqualTo("82%");
        assertThat(pageObject.getTotalScores()).containsExactly("94%", "92%", "50%", "94%");

        verifyTests(pageObject);
        verifyCoverage(pageObject);
        verifyMutationCoverage(pageObject);
        verifyAnalysis(pageObject);
    }

    private void verifyTests(final AutoGradePageObject pageObject) {
        assertThatTestResultsHeaderIsCorrect(pageObject.getTestHeaders());
        assertThat(pageObject.getTestBody().get("Tests (1/1/3)")).containsExactly(3, 1, 1, 5, -6);
        assertThat(pageObject.getTestFooter()).containsExactly("0", "-5", "-1", "n/a", "n/a");
    }

    private void verifyCoverage(final AutoGradePageObject pageObject) {
        assertThatCodeCoverageHeaderIsCorrect(pageObject.getCoverageHeaders());
        assertThat(pageObject.getCoverageBody().get("LineCoverage")).containsExactly(91, 9, -9);
        assertThat(pageObject.getCoverageBody().get("BranchCoverage")).containsExactly(94, 6, -6);
        assertThat(pageObject.getCoverageFooter()).containsExactly("0", "-1", "n/a");
    }

    private void verifyMutationCoverage(final AutoGradePageObject pageObject) {
        assertThatPITMutationsHeaderIsCorrect(pageObject.getPitHeaders());
        assertThat(pageObject.getPitBody().get("MutationCoverage")).containsExactly(50, 50, -50);
        assertThat(pageObject.getPitFooter()).containsExactly("0", "-1", "n/a");
    }

    private void verifyAnalysis(final AutoGradePageObject pageObject) {
        assertThatStaticAnalysisHeaderIsCorrect(pageObject.getAnalysisHeaders());
        assertThat(pageObject.getAnalysisBody().get("CheckStyle")).containsExactly(6, 0, 2, 3, 11, 24);
        assertThat(pageObject.getAnalysisBody().get("PMD")).containsExactly(0, 0, 3, 0, 3, 9);
        assertThat(pageObject.getAnalysisBody().get("CPD")).containsExactly(0, 5, 9, 6, 20, 61);
        assertThat(pageObject.getAnalysisBody().get("FindBugs")).containsExactly(0, 0, 0, 0, 0, 0);
        assertThat(pageObject.getAnalysisFooter()).containsExactly("1", "2", "3", "4", "n/a", "n/a");
    }

    private void configurePipeline(final WorkflowJob job, final String configuration, final String...files) {
        job.script.set("node {\n"
                + createReportFilesStep(job, files)
                + "junit testResults: '**/TEST-*'\n"
                + "recordIssues tool: checkStyle(pattern: '**/checkstyle*'), skipPublishingChecks: true\n"
                + "recordCoverage tools: [[parser: 'JACOCO', pattern:'**/jacoco*']], sourceCodeRetention: 'EVERY_BUILD', name: 'Code Coverage'\n"
                + "recordIssues tool: pmdParser(pattern: '**/pmd*'), skipPublishingChecks: true\n"
                + "recordIssues tools: [cpd(pattern: '**/cpd*', highThreshold:8, normalThreshold:3), findBugs()], aggregatingResults: 'false', skipPublishingChecks: true \n"
                + "recordCoverage tools: [[parser: 'PIT', pattern:'**/mutations*']], id: 'pit', sourceCodeRetention: 'EVERY_BUILD', name: 'Mutation Coverage'\n"
                + "autoGrade('" + StringUtils.deleteWhitespace(configuration) + "')\n"
                + "}");
    }

    private Build shouldBuildJobUnstable(final Job job) {
        Build build = job.startBuild().waitUntilFinished();
        assertThat(build.isUnstable()).isTrue();
        return build;
    }

    private StringBuilder createReportFilesStep(final WorkflowJob job, final String...files) {
        StringBuilder resourceCopySteps = new StringBuilder();
        Arrays.stream(files).forEach(fileName ->
                resourceCopySteps.append(job.copyResourceStep(AUTOGRADING_PLUGIN_PREFIX + fileName)));
        return resourceCopySteps;
    }

    private URL buildAutoGradeURLFromJob(final Job job) {
        try {
            return new URL(job.url.toString() + "/autograding");
        }
        catch (MalformedURLException x) {
            throw new AssertionError(x);
        }
    }

    private void assertThatTestResultsHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Name", "Passed", "Failed", "Skipped", "Total", "Score Impact");
    }

    private void assertThatCodeCoverageHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Type", "Covered Percentage", "Missed Percentage", "Score Impact");
    }

    private void assertThatPITMutationsHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Type", "Killed Percentage", "Survived Percentage", "Score Impact");
    }

    private void assertThatStaticAnalysisHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Tool", "Errors", "High", "Normal", "Low", "Total", "Score Impact");
    }
}
