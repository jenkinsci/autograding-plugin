package io.jenkins.plugins.grading; // NOPMD

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.JacksonFacade;
import edu.hm.hafner.util.FilteredLog;
import edu.umd.cs.findbugs.annotations.NonNull;

import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;

import io.jenkins.plugins.util.LogHandler;

/**
 * This recorder gathers all the needed results of previous run check in the job. Inputs are Saved, and Quality Score is
 * computed on base of defined configurations.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class AutoGrader extends Recorder implements SimpleBuildStep {
    static final String LOG_TITLE = "Autograding Jenkins build results";
    private final String configuration;

    /**
     * Creates a new instance of {@link AutoGrader}.
     *
     * @param configuration
     *         the configuration to use, must be in JSON format
     */
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
        FilteredLog log = new FilteredLog(LOG_TITLE);

        JacksonFacade jackson = new JacksonFacade();
        AggregatedScore score = new AggregatedScore(configuration, log);

        log.logInfo("Test Configuration: %s", jackson.toJson(score.getTestConfiguration()));
        score.addTestScores(new JenkinsTestSupplier(run));
        log.logInfo("Code Coverage Configuration: %s", jackson.toJson(score.getCoverageConfiguration()));
        score.addCoverageScores(new JenkinsCoverageSupplier(run));
        log.logInfo("PIT Mutation Coverage Configuration: %s", jackson.toJson(score.getPitConfiguration()));
        score.addPitScores(new JenkinsPitSupplier(run));
        log.logInfo("Static Analysis Configuration: %s", jackson.toJson(score.getAnalysisConfiguration()));
        JenkinsAnalysisSupplier analysisScores = new JenkinsAnalysisSupplier(run);
        score.addAnalysisScores(analysisScores);

        LogHandler logHandler = new LogHandler(listener, "Autograding");
        logHandler.log(log);

        run.addAction(new AutoGradingBuildAction(run, score));

        AutoGradingChecksPublisher checksPublisher = new AutoGradingChecksPublisher();
        checksPublisher.publishChecks(run, listener, score, analysisScores.getReports());
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
