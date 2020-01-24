package io.jenkins.plugins.quality.core;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.tasks.junit.TestResultAction;
import io.jenkins.plugins.analysis.warnings.Pit;
import io.jenkins.plugins.coverage.CoverageAction;
import io.jenkins.plugins.coverage.targets.CoverageResult;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
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
        List<TestResultAction> testActions = run.getActions(TestResultAction.class);
        List<PitBuildAction> pitAction = run.getActions(PitBuildAction.class);
        List<CoverageAction> coverageActions = run.getActions(CoverageAction.class);

        Map<String, Configuration> configs = new HashMap<>();
        List<Integer> maxScore = new ArrayList<>();
        Map<String, BaseResults> base = new HashMap<>();
        Score score = new Score();

        //maxScore  holen und in Score speichern

        //Defaults lesen und Rechnen
        DefaultChecks checks = new DefaultChecks();
        checks.compute(configs, actions, base, score);

        //PIT lesen und rechnen
        PITs pits = new PITs();
        pits.compute(configs, pitAction, base, score);

        //JUNIT lesen und rechnen
        JUNITs junitChecks = new JUNITs();
        junitChecks.compute(configs, testActions, base, score);

        /*//code-coverage lesen und rechnen
        CoCos cocos = new CoCos();
        cocos.compute(configs, coverageActions,base, score);
        */
        score.addConfigs(configs);
        score.addBases(base);

        listener.getLogger().println("[CodeQuality] -> found " + actions.size() + " checks");
        listener.getLogger().println("[CodeQuality] Code Quality Results are: ");

        //final int finalScore = computeScore(actions, maxScore.get(0), configs, listener);

        for (ResultAction action : actions) {
            // BaseResults baseResult = new BaseResults();
            //baseResult.setId(action.getResult().getId());
            //baseResult.setTotalErrors(action.getResult().getTotalErrorsSize());
            //baseResult.setTotalHighs(action.getResult().getTotalHighPrioritySize());
            //baseResult.setTotalNormals(action.getResult().getTotalNormalPrioritySize());
            //baseResult.setTotalLows(action.getResult().getTotalLowPrioritySize());
            listener.getLogger().println("[CodeQuality] For " + action.getResult().getId() + " the following issues where found:");
            listener.getLogger().println("[CodeQuality] Number of Errors: " + action.getResult().getTotalErrorsSize());
            listener.getLogger().println("[CodeQuality] Number of High Issues: " + action.getResult().getTotalHighPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Normal Issues: " + action.getResult().getTotalNormalPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Low Issues: " + action.getResult().getTotalLowPrioritySize());
            // base.add(baseResult);
        }

        listener.getLogger().println("[CodeQuality] Total score achieved: Points");
        //listener.getLogger().println(actions.stream().map(ResultAction::getId).collect(Collectors.joining()));

        //Score scores = new Score(finalScore, maxScore.get(0), configs, base);

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
