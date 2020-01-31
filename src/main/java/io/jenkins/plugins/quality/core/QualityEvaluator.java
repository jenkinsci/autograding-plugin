package io.jenkins.plugins.quality.core;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.tasks.junit.TestResultAction;
import io.jenkins.plugins.coverage.CoverageAction;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
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
        Map<String, BaseResults> base = new HashMap<>();
        List<ResultAction> actions = run.getActions(ResultAction.class);
        List<PitBuildAction> pitAction = run.getActions(PitBuildAction.class);
        List<TestResultAction> testActions = run.getActions(TestResultAction.class);
        List<CoverageAction> coverageActions = run.getActions(CoverageAction.class);

        //read configs from XML File
        ConfigXmlStream configReader = new ConfigXmlStream();
        Configuration configs = configReader.read(Paths.get(workspace +  "\\Config.xml"));
        listener.getLogger().println("[CodeQuality] Read Configs:");
        listener.getLogger().println("[CodeQuality] MaxScore "+configs.getMaxScore());

        Score score = new Score(configs.getMaxScore());
        score.setMaxScore(configs.getMaxScore());
        score.addConfigs(configs);

        //Defaults Rechnen
        DefaultChecks checks = new DefaultChecks();
        checks.compute(configs, actions, base, score, listener);

        //PIT lesen und rechnen
        PITs pits = new PITs();
        // pits.compute(configs, pitAction, base, score);

        //JUNIT lesen und rechnen
        TestRes junitChecks = new TestRes();
        junitChecks.compute(configs, testActions, base, score);

        //code-coverage lesen und rechnen
        CoCos cocos = new CoCos();
        // cocos.compute(configs, coverageActions,base, score);

        score.addBases(base);

        listener.getLogger().println("[CodeQuality] -> found " + actions.size() + " checks");
        listener.getLogger().println("[CodeQuality] Code Quality Results are: ");


        listener.getLogger().println("[CodeQuality] Total score achieved: "+score.getScore()+"Points");

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
