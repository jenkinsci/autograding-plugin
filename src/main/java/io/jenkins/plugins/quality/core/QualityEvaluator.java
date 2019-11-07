package io.jenkins.plugins.quality.core;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

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

public class QualityEvaluator extends Recorder implements SimpleBuildStep {
    /**
     * Creates a new instance of {@link  QualityEvaluator}.
     */
    @DataBoundConstructor
    public QualityEvaluator() {
        super();

        // empty constructor required for Stapler
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace,
            @Nonnull final Launcher launcher,
            @Nonnull final TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Hello Code Quality!");

        List<ResultAction> actions = run.getActions(ResultAction.class);
        listener.getLogger().println(actions.stream().map(ResultAction::getId).collect(Collectors.joining()));
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
