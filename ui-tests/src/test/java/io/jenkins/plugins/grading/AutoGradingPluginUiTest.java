package io.jenkins.plugins.grading;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import org.jenkinsci.test.acceptance.junit.AbstractJUnitTest;
import org.jenkinsci.test.acceptance.junit.WithPlugins;
import org.jenkinsci.test.acceptance.plugins.warnings_ng.IssuesRecorder;
import org.jenkinsci.test.acceptance.plugins.warnings_ng.ScrollerUtil;
import org.jenkinsci.test.acceptance.po.Build;
import org.jenkinsci.test.acceptance.po.FreeStyleJob;
import org.jenkinsci.test.acceptance.po.Job;
import org.jenkinsci.test.acceptance.po.PageObject;

import static org.assertj.core.api.Assertions.*;

/**
 * Acceptance tests for the AutoGrading Plugin.
 *
 * @author Lukas Kirner
 */
@WithPlugins("autograding")
public class AutoGradingPluginUiTest extends AbstractJUnitTest {
    private static final String AUTOGRADING_PLUGIN_PREFIX = "/autograding_test/";
    private static final String HEADER_TOTAL_SCORE = "Total Score";
    private static final String HEADER_TEST_RESULTS = "Test Results";
    private static final String HEADER_CODE_COVERAGE = "Code Coverage";
    private static final String HEADER_PIT_MUTATIONS = "PIT Mutations";
    private static final String HEADER_STATIC_ANALYSIS = "Static Analysis";

    /**
     * Test the basic view.
     */
    @Test
    public void basicViewTest() {
        FreeStyleJob job = createFreeStyleJob();
        job.addPublisher(AutoGradeStep.class, grade -> grade.setConfiguration("{}"));
        job.save();
        Build build = shouldBuildJobSuccessfully(job);

        AutoGradePageObject pageObject = new AutoGradePageObject(build, buildAutoGradeURLFromJob(job));

        assertThatCardHeadersAreCorrect(pageObject);
        assertThat(pageObject.getCoverageBody()).isEmpty();
        assertThat(pageObject.getTestBody()).isEmpty();
        assertThat(pageObject.getPitBody()).isEmpty();
        assertThat(pageObject.getAnalysisBody()).isEmpty();
    }

    /**
     * Test Static Analysis card.
     */
    @Test
    public void checkstyle() {
        FreeStyleJob job = createFreeStyleJob("checkstyle-result.xml");
        job.addPublisher(IssuesRecorder.class, recorder -> recorder.setTool("CheckStyle"));
        job.addPublisher(AutoGradeStep.class, grade -> grade.setConfiguration("{\"analysis\":{\"maxScore\":100,\"errorImpact\":-10,\"highImpact\":-5,\"normalImpact\":-2,\"lowImpact\":-1}}"));

        job.save();
        Build build = shouldBuildJobSuccessfully(job);

        AutoGradePageObject pageObject = new AutoGradePageObject(build, buildAutoGradeURLFromJob(job));
        assertThatCardHeadersAreCorrect(pageObject);
    }

    private void assertThatCardHeadersAreCorrect(final AutoGradePageObject pageObject) {
        assertThat(pageObject.getCardHeaders()).containsExactlyInAnyOrder(HEADER_TOTAL_SCORE, HEADER_TEST_RESULTS,
                HEADER_CODE_COVERAGE, HEADER_PIT_MUTATIONS, HEADER_STATIC_ANALYSIS);
    }

    private Build shouldBuildJobSuccessfully(final Job job) {
        Build build = job.startBuild().waitUntilFinished();
        assertThat(build.isSuccess()).isTrue();
        return build;
    }

    private FreeStyleJob createFreeStyleJob(final String... resourcesToCopy) {
        FreeStyleJob job = jenkins.getJobs().create(FreeStyleJob.class);
        ScrollerUtil.hideScrollerTabBar(driver);
        for (String resource : resourcesToCopy) {
            job.copyResource(AUTOGRADING_PLUGIN_PREFIX + resource);
        }
        return job;
    }

    private URL buildAutoGradeURLFromJob(final Job job) {
        try {
            return new URL(job.url.toString() + "/autograding");
        }
        catch (MalformedURLException x) {
            return null;
        }
    }
}
