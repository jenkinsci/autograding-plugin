package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.List;

import edu.hm.hafner.util.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.junit.TestResultAction;
import jenkins.tasks.SimpleBuildStep;

import io.jenkins.plugins.analysis.core.model.ResultAction;
import io.jenkins.plugins.coverage.CoverageAction;
import io.jenkins.plugins.coverage.targets.CoverageElement;
import io.jenkins.plugins.util.LogHandler;

/**
 * This recorder gathers all the needed results of previous run check in the job. Inputs are Saved, and Quality Score is
 * computed on base of defined configurations.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGrader extends Recorder implements SimpleBuildStep {
    private final String configuration;

    /**
     * Creates a new instance of {@link AutoGrader}.
     *
     * @param configuration
     *         the configuration to use, must be in JSON format
     */
    // TODO: Create JSON schema
    @DataBoundConstructor
    public AutoGrader(final String configuration) {
        super();

        this.configuration = configuration;
    }

    public String getConfiguration() {
        return configuration;
    }

    @Override
    public void perform(@NonNull final Run<?, ?> run, @NonNull final FilePath workspace,
            @NonNull final Launcher launcher, @NonNull final TaskListener listener) {
        LogHandler logHandler = new LogHandler(listener, "Autograding");
        logHandler.log("Using configuration '%s'", configuration);

        try {
            JSONObject gradingConfiguration = JSONObject.fromObject(configuration);

            Score score = new Score();
            JSONObject analysisConfiguration = (JSONObject) gradingConfiguration.get("analysis");
            if (analysisConfiguration != null) {
                gradeAnalysisResults(run, score, analysisConfiguration, logHandler);
            }
            else {
                logHandler.log("Skipping static analysis results");
            }

            JSONObject testConfiguration = (JSONObject) gradingConfiguration.get("tests");
            if (testConfiguration != null) {
                gradeTestResults(run, score, testConfiguration, logHandler);
            }
            else {
                logHandler.log("Skipping test results");
            }

            JSONObject coverageConfiguration = (JSONObject) gradingConfiguration.get("coverage");
            if (coverageConfiguration != null) {
                gradeCoverageResults(run, score, coverageConfiguration, logHandler);
            }
            else {
                logHandler.log("Skipping coverage results");
            }

            JSONObject pitConfiguration = (JSONObject) gradingConfiguration.get("pit");
            if (pitConfiguration != null) {
                gradePitResults(run, score, pitConfiguration, logHandler);
            }
            else {
                logHandler.log("Skipping mutation coverage results");
            }

            run.addAction(new AutoGradingBuildAction(run, score));
        }
        catch (JSONException exception) {
            throw new IllegalArgumentException("Invalid configuration: " + configuration);
        }
   }

    @VisibleForTesting
    private void gradePitResults(@NonNull final Run<?, ?> run,
            final Score actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
        PitConfiguration pitConfiguration = PitConfiguration.from(jsonConfiguration);
        PitBuildAction action = run.getAction(PitBuildAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Mutation coverage scoring has been enabled, but no PIT results have been found.");
        }
        logHandler.log("Grading PIT mutation results " + action.getDisplayName());
        PitScore score = new PitScore(pitConfiguration, action);
        int total = actualScore.addPitTotal(pitConfiguration, score);
        logHandler.log("-> Score %d - from recorded PIT mutation results: %d, %d, %d, %d",
                score.getTotalImpact(), score.getMutationsSize(), score.getUndetectedSize(),
                score.getDetectedSize(), score.getRatio());
        logHandler.log("Total score for mutation coverage results: " + total);
    }

    @VisibleForTesting
    private void gradeCoverageResults(@NonNull final Run<?, ?> run,
            final Score actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
        CoverageConfiguration coverageConfiguration = CoverageConfiguration.from(jsonConfiguration);
        CoverageAction action = run.getAction(CoverageAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Coverage scoring has been enabled, but no coverage results have been found.");
        }
        logHandler.log("Grading coverage results " + action.getDisplayName());
        CoverageScore score = new CoverageScore(coverageConfiguration,
                action.getResult().getCoverage(CoverageElement.LINE));
        int total = actualScore.addCoverageTotal(coverageConfiguration, score);

        logHandler.log("-> Score %d - from recorded coverage results: %d%%",
                score.getTotalImpact(), score.getCoveredSize());
        logHandler.log("Total score for coverage results: " + total);
    }

    @VisibleForTesting
    private void gradeTestResults(@NonNull final Run<?, ?> run,
            final Score actualScore, final JSONObject testConfiguration, final LogHandler logHandler) {
        TestResultAction action = run.getAction(TestResultAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Test scoring has been enabled, but no test results have been found.");
        }
        TestConfiguration testsConfiguration = TestConfiguration.from(testConfiguration);
        logHandler.log("Grading test results " + action.getDisplayName());
        TestScore score = new TestScore(testsConfiguration, action);
        int total = actualScore.addTestsTotal(testsConfiguration, score);

        logHandler.log("-> Score %d - from recorded test results: %d, %d, %d, %d",
                score.getTotalImpact(), score.getTotalSize(), score.getPassedSize(),
                score.getFailedSize(), score.getSkippedSize());
        logHandler.log("Total score for test results: " + total);
    }

    @VisibleForTesting
    void gradeAnalysisResults(final Run<?, ?> run,
            final Score actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
        List<ResultAction> actions = run.getActions(ResultAction.class);
        AnalysisConfiguration analysisConfiguration = AnalysisConfiguration.from(jsonConfiguration);
        List<AnalysisScore> analysisScores = new ArrayList<>();
        for (ResultAction action : actions) {
            String name = action.getLabelProvider().getName();
            logHandler.log("Grading static analysis results for " + name);
            AnalysisScore score = new AnalysisScore(name, analysisConfiguration, action.getResult());
            analysisScores.add(score);
            logHandler.log("-> Score %d (warnings distribution err:%d, high:%d, normal:%d, low:%d)",
                    score.getTotalImpact(), score.getErrorsSize(), score.getHighPrioritySize(),
                    score.getNormalPrioritySize(), score.getLowPrioritySize());
        }
        int total = actualScore.addAnalysisTotal(analysisConfiguration, analysisScores);
        logHandler.log("Total score for static analysis results: %d of %d",
                total, analysisConfiguration.getMaxScore());
    }

    /** Descriptor for this step. */
    @Extension(ordinal = -100_000)
    @Symbol("autoGrade")
    @SuppressWarnings("unused") // most methods are used by the corresponding jelly view
    public static class Descriptor extends BuildStepDescriptor<Publisher> {
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Action_Name();
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
