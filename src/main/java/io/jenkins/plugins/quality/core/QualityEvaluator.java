package io.jenkins.plugins.quality.core;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import edu.hm.hafner.analysis.Report;
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

import io.jenkins.plugins.analysis.core.model.ResultAction;
import sun.security.ec.point.ProjectivePoint;

public class QualityEvaluator extends Recorder implements SimpleBuildStep {
    @DataBoundConstructor
    /**
     * Creates a new instance of {@link  QualityEvaluator}.
     */

    public QualityEvaluator() {
        super();

        // empty constructor required for Stapler
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace,
            @Nonnull final Launcher launcher,
            @Nonnull final TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("[CodeQuality] Starting extraction of previous performed checks");

        List<ResultAction> actions = run.getActions(ResultAction.class);

        listener.getLogger().println("[CodeQuality] -> found "+actions.size()+" checks");
        listener.getLogger().println("[CodeQuality] Code Quality Results are: ");

        final int score = computeScore(actions, listener);

        for(ResultAction action : actions){
            listener.getLogger().println("[CodeQuality] For "+action.getResult().getId()+ " the following issues where found:");
            listener.getLogger().println("[CodeQuality] Number of Errors: "+action.getResult().getTotalErrorsSize());
            listener.getLogger().println("[CodeQuality] Number of High Issues: "+action.getResult().getTotalHighPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Normal Issues: "+action.getResult().getTotalNormalPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Low Issues: "+action.getResult().getTotalLowPrioritySize());
        }

        listener.getLogger().println("[CodeQuality] Total score achieved: "+ score +" Points");
        //listener.getLogger().println(actions.stream().map(ResultAction::getId).collect(Collectors.joining()));
    }

    private int computeScore(List<ResultAction> actions,  @Nonnull final TaskListener listener) {
        int score = 100;
        for(ResultAction action : actions){
            if(action.getResult().getTotalErrorsSize() > 0) {
                score = score - (action.getResult().getTotalErrorsSize() * 4);
            }
            if(action.getResult().getTotalErrorsSize() > 0) {
                score = score - (action.getResult().getTotalHighPrioritySize() * 3);
            }
            if(action.getResult().getTotalErrorsSize() > 0) {
                score = score - (action.getResult().getTotalNormalPrioritySize() * 2);
            }
            if(action.getResult().getTotalErrorsSize() > 0) {
                score = score - action.getResult().getTotalLowPrioritySize();
            }

        }
        return score;
    }

    /**
     * Descriptor for this step: defines the context and the UI elements.
     */
    @Extension(ordinal = -100_000)
    @Symbol("computeQuality")
    @SuppressWarnings("unused") // most methods are used by the corresponding jelly view
    public static class Descriptor extends BuildStepDescriptor<Publisher> {
        @NonNull
        @Override
        public String getDisplayName() {
            return "Compute code quality";
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
