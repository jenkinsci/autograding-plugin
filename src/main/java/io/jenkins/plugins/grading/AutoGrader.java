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
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
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

            AggregatedScore score = new AggregatedScore();
            JSONObject analysisConfiguration = (JSONObject) gradingConfiguration.get("analysis");
            if (analysisConfiguration == null) {
                logHandler.log("Skipping static analysis results");
            }
            else {
                gradeAnalysisResults(run, score, analysisConfiguration, logHandler);
            }

            JSONObject testConfiguration = (JSONObject) gradingConfiguration.get("tests");
            if (testConfiguration == null) {
                logHandler.log("Skipping test results");
            }
            else {
                gradeTestResults(run, score, testConfiguration, logHandler);
            }

            JSONObject coverageConfiguration = (JSONObject) gradingConfiguration.get("coverage");
            if (coverageConfiguration == null) {
                logHandler.log("Skipping coverage results");
            }
            else {
                gradeCoverageResults(run, score, coverageConfiguration, logHandler);
            }

            JSONObject pitConfiguration = (JSONObject) gradingConfiguration.get("pit");
            if (pitConfiguration == null) {
                logHandler.log("Skipping mutation coverage results");
            }
            else {
                gradePitResults(run, score, pitConfiguration, logHandler);
            }

            run.addAction(new AutoGradingBuildAction(run, score));
        }
        catch (JSONException exception) {
            throw new IllegalArgumentException("Invalid configuration: " + configuration, exception);
        }
    }

    @VisibleForTesting
    private void gradePitResults(@NonNull final Run<?, ?> run,
            final AggregatedScore actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
        PitBuildAction action = run.getAction(PitBuildAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Mutation coverage scoring has been enabled, but no PIT results have been found.");
        }

        logHandler.log("Grading PIT mutation results " + action.getDisplayName());
        PitConfiguration pitConfiguration = PitConfiguration.from(jsonConfiguration);
        PitScore score = new PitScore(pitConfiguration, action);
        int total = actualScore.addPitTotal(pitConfiguration, score);

        logHandler.log("-> Score %d - from recorded PIT mutation results: %d, %d, %d, %d",
                score.getTotalImpact(), score.getMutationsSize(), score.getUndetectedSize(),
                score.getDetectedSize(), score.getRatio());
        logHandler.log("Total score for mutation coverage results: " + total);
    }

    @VisibleForTesting
    private void gradeCoverageResults(@NonNull final Run<?, ?> run,
            final AggregatedScore actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
        CoverageAction action = run.getAction(CoverageAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Coverage scoring has been enabled, but no coverage results have been found.");
        }

        logHandler.log("Grading coverage results " + action.getDisplayName());
        CoverageConfiguration coverageConfiguration = CoverageConfiguration.from(jsonConfiguration);

        CoverageScore lineCoverage = createCoverageScore(action, coverageConfiguration, CoverageElement.LINE);
        logHandler.log("-> Score %d - from recorded line coverage results: %d%%",
                lineCoverage.getTotalImpact(), lineCoverage.getCoveredSize());

        CoverageScore branchCoverage = createCoverageScore(action, coverageConfiguration, CoverageElement.CONDITIONAL);
        logHandler.log("-> Score %d - from recorded branch coverage results: %d%%",
                branchCoverage.getTotalImpact(), branchCoverage.getCoveredSize());

        int total = actualScore.addCoverageTotal(coverageConfiguration, lineCoverage, branchCoverage);

        logHandler.log("-> Score %d - from recorded coverage results: %d%%",
                lineCoverage.getTotalImpact(), lineCoverage.getCoveredSize());
        logHandler.log("Total score for coverage results: " + total);
    }

    private CoverageScore createCoverageScore(final CoverageAction action,
            final CoverageConfiguration coverageConfiguration, final CoverageElement type) {
        return new CoverageScore(type.getName(), coverageConfiguration, action.getResult().getCoverage(type));
    }

    @VisibleForTesting
    private void gradeTestResults(@NonNull final Run<?, ?> run,
            final AggregatedScore actualScore, final JSONObject testConfiguration, final LogHandler logHandler) {
        TestResultAction action = run.getAction(TestResultAction.class);
        if (action == null) {
            throw new IllegalArgumentException(
                    "Test scoring has been enabled, but no test results have been found.");
        }

        logHandler.log("Grading test results " + action.getDisplayName());
        TestConfiguration testsConfiguration = TestConfiguration.from(testConfiguration);
        TestScore score = new TestScore(testsConfiguration, action);
        int total = actualScore.addTestsTotal(testsConfiguration, score);

        logHandler.log("-> Score %d - from recorded test results: %d, %d, %d, %d",
                score.getTotalImpact(), score.getTotalSize(), score.getPassedSize(),
                score.getFailedSize(), score.getSkippedSize());
        logHandler.log("Total score for test results: " + total);
    }

    @VisibleForTesting
    void gradeAnalysisResults(final Run<?, ?> run,
            final AggregatedScore actualScore, final JSONObject jsonConfiguration, final LogHandler logHandler) {
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

    @Override
    public AutoGrader.Descriptor getDescriptor() {
        return (AutoGrader.Descriptor) super.getDescriptor();
    }

    /** Descriptor for this step. */
    @Extension(ordinal = -100_000)
    @Symbol("autoGrade")
    @SuppressWarnings("unused") // most methods are used by the corresponding jelly view
    public static class Descriptor extends BuildStepDescriptor<Publisher> {
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Step_Name();
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
