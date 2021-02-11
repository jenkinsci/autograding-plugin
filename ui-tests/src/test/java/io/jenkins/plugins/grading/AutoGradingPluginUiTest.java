package io.jenkins.plugins.grading;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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
@WithPlugins({"autograding", "warnings-ng", "junit", "pitmutation", "code-coverage-api", "pipeline-stage-step", "workflow-durable-task-step", "workflow-basic-steps"})
public class AutoGradingPluginUiTest extends AbstractJUnitTest {
    private static final String AUTOGRADING_PLUGIN_PREFIX = "/autograding_test/";
    private static final String CONFIGURATION = "{\"analysis\":{\"maxScore\":100,\"errorImpact\":-5,\"highImpact\":-3,\"normalImpact\":-2,\"lowImpact\":-1}, \"coverage\":{\"maxScore\":200,\"coveredPercentageImpact\":1,\"missedPercentageImpact\":0}, \"tests\":{\"maxScore\":100,\"passedImpact\":1,\"failureImpact\":-10,\"skippedImpact\":-1}, \"pit\":{\"maxScore\":100,\"detectedImpact\":1,\"undetectedImpact\":-10,\"ratioImpact\":0}}";

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

        assertThat(pageObject.getTotalScoreInPercent()).isEqualTo("73%");
        assertThat(pageObject.getTotalScores()).containsExactly("92%", "83%", "91%", "18%");

        verifyTests(pageObject);
        verifyCoverage(pageObject);
        verifyMutationCoverage(pageObject);
        verifyAnalysis(pageObject);
    }

    private void verifyTests(final AutoGradePageObject pageObject) {
        assertThatTestResultsHeaderIsCorrect(pageObject.getTestHeaders());
        assertThat(pageObject.getTestBody().get("tests")).containsExactly(3, 1, 1, 5, -8);
        assertThat(pageObject.getTestFooter()).containsExactly("1", "-10", "-1", "n/a", "n/a");
    }

    private void verifyMutationCoverage(final AutoGradePageObject pageObject) {
        assertThatPITMutationsHeaderIsCorrect(pageObject.getPitHeaders());
        assertThat(pageObject.getPitBody().get("pit")).containsExactly(1, 1, 50, 50, -9);
        assertThat(pageObject.getPitFooter()).containsExactly("1", "-10", "0", "0", "n/a");
    }

    private void verifyCoverage(final AutoGradePageObject pageObject) {
        assertThatCodeCoverageHeaderIsCorrect(pageObject.getCoverageHeaders());
        assertThat(pageObject.getCoverageBody().get("Line Coverage")).containsExactly(83, 17, 83);
        assertThat(pageObject.getCoverageBody().get("Conditional Coverage")).containsExactly(83, 17, 83);
        assertThat(pageObject.getCoverageFooter()).containsExactly("1", "0", "n/a");
    }

    private void verifyAnalysis(final AutoGradePageObject pageObject) {
        assertThatStaticAnalysisHeaderIsCorrect(pageObject.getAnalysisHeaders());
        assertThat(pageObject.getAnalysisBody().get("CheckStyle")).containsExactly(6, 0, 2, 3, 11, -37);
        assertThat(pageObject.getAnalysisBody().get("PMD")).containsExactly(0, 0, 3, 0, 3, -6);
        assertThat(pageObject.getAnalysisBody().get("CPD")).containsExactly(0, 5, 9, 6, 20, -39);
        assertThat(pageObject.getAnalysisBody().get("FindBugs")).containsExactly(0, 0, 0, 0, 0, 0);
        assertThat(pageObject.getAnalysisFooter()).containsExactly("-5", "-3", "-2", "-1", "n/a", "n/a");
    }

    private void configurePipeline(final WorkflowJob job, final String configuration, final String...files) {
        job.script.set("node {\n"
                + createReportFilesStep(job, 1, files)
                + "junit testResults: '**/TEST-*'\n"
                + "recordIssues tool: checkStyle(pattern: '**/checkstyle*'), skipPublishingChecks: true\n"
                + "step([$class: 'PitPublisher', mutationStatsFile: '**/mutations*'])\n"
                + "recordIssues tool: pmdParser(pattern: '**/pmd*'), skipPublishingChecks: true\n"
                + "recordIssues tools: [cpd(pattern: '**/cpd*', highThreshold:8, normalThreshold:3), findBugs()], aggregatingResults: 'false', skipPublishingChecks: true \n"
                + "publishCoverage adapters: [jacocoAdapter('**/jacoco*')], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), skipPublishingChecks: true\n"
                + "autoGrade('" + configuration + "')\n"
                + "}");
    }

    private void configurePipelineWithoutJUnitTests(final WorkflowJob job, final String configuration, final String...files) {
        job.script.set("node {\n"
                + createReportFilesStep(job, 1, files)
                + "recordIssues tool: checkStyle(pattern: '**/checkstyle*'), skipPublishingChecks: true\n"
                + "step([$class: 'PitPublisher', mutationStatsFile: '**/mutations*'])\n"
                + "recordIssues tool: pmdParser(pattern: '**/pmd*'), skipPublishingChecks: true\n"
                + "recordIssues tools: [cpd(pattern: '**/cpd*', highThreshold:8, normalThreshold:3), findBugs()], aggregatingResults: 'false', skipPublishingChecks: true\n"
                + "publishCoverage adapters: [jacocoAdapter('**/jacoco*')], sourceFileResolver: sourceFiles('STORE_ALL_BUILD'), skipPublishingChecks: true\n"
                + "autoGrade('" + configuration + "')\n"
                + "}");
    }

    private Build shouldBuildJobSuccessfully(final Job job) {
        Build build = job.startBuild().waitUntilFinished();
        assertThat(build.isSuccess()).isTrue();
        return build;
    }

    private Build shouldBuildJobUnstable(final Job job) {
        Build build = job.startBuild().waitUntilFinished();
        assertThat(build.isUnstable()).isTrue();
        return build;
    }

    private StringBuilder createReportFilesStep(final WorkflowJob job, final int build, final String...files) {
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
            throw new AssertionError((x));
        }
    }

    private void assertThatTestResultsHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Name", "Passed", "Failed", "Skipped", "Total", "Score Impact");
    }

    private void assertThatCodeCoverageHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Type", "Covered Percentage", "Missed Percentage", "Score Impact");
    }

    private void assertThatPITMutationsHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Type", "Detected", "Undetected", "Detected Percentage", "Undetected Percentage", "Score Impact");
    }

    private void assertThatStaticAnalysisHeaderIsCorrect(final List<String> headers) {
        assertThat(headers).containsExactly("Tool", "Errors", "High", "Normal", "Low", "Total", "Score Impact");
    }
}
