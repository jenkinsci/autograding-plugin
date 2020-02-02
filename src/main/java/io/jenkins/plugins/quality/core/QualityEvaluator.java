package io.jenkins.plugins.quality.core;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlType;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.tasks.junit.TestResultAction;
import io.jenkins.plugins.coverage.CoverageAction;
import jenkins.model.DefaultUserCanonicalIdResolver;
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

/**
 * This Recorder gathers all the needed results of previous run check in the job.
 * Inputs are Saved, and Quality Score is computed on base of defined configurations.
 *
 * @author Eva-Maria Zeintl
 */
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

        listener.getLogger().println("[CodeQuality] Starting extraction of previous performed checks");
        List<ResultAction> actions = run.getActions(ResultAction.class);
        List<PitBuildAction> pitAction = run.getActions(PitBuildAction.class);
        List<TestResultAction> testActions = run.getActions(TestResultAction.class);
        List<CoverageAction> coverageActions = run.getActions(CoverageAction.class);
        List<DefaultChecks> defaultBase = new ArrayList<>();
        List<CoCos> cocoBases = new ArrayList<>();
        List<PITs> pitBases = new ArrayList<>();
        List<TestRes> junitBases = new ArrayList<>();


        //read configs from XML File
        ConfigXmlStream configReader = new ConfigXmlStream();
        Configuration configs = configReader.read(Paths.get(workspace + "\\Config.xml"));
        listener.getLogger().println("[CodeQuality] Read Configs:");
        listener.getLogger().println("[CodeQuality] MaxScore " + configs.getMaxScore());

        Score score = new Score(configs.getMaxScore());
        score.addConfigs(configs);

        //Defaults Rechnen
        saveDefaultBase(actions, defaultBase);
        for (DefaultChecks base : defaultBase) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addDefaultBase(base);
        }

        //PIT lesen und rechnen
        savePitBase(pitAction, pitBases);
        for (PITs base : pitBases) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addPitBase(base);
        }

        //JUNIT lesen und rechnen
        saveJunitBase(testActions, junitBases);
        for (TestRes base : junitBases) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addJunitBase(base);
        }

        //code-coverage lesen und rechnen
        saveCocoBase(coverageActions, cocoBases);
        for (CoCos base : cocoBases) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addCocoBase(base);
        }


        listener.getLogger().println("[CodeQuality] Code Quality Results are: ");
        listener.getLogger().println("[CodeQuality] Total score achieved: " + score.getScore() + "Points");

        run.addAction(new ScoreBuildAction(run, score));
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
            return "Code Quality Score";
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
    }


    private void saveDefaultBase(List<ResultAction> actions, List<DefaultChecks> defaultBase) {
        for (ResultAction action : actions) {
            //save base Results
            defaultBase.add(new DefaultChecks(action.getId(), action.getResult().getTotalErrorsSize(),
                    action.getResult().getTotalHighPrioritySize(), action.getResult().getTotalNormalPrioritySize(),
                    action.getResult().getTotalLowPrioritySize(), action.getResult().getTotalSize()));
        }
    }

    private void savePitBase(List<PitBuildAction> pitAction, List<PITs> pitBases) {
        for (PitBuildAction action : pitAction) {
            //save base Results
            pitBases.add(new PITs(action.getDisplayName(), action.getReport().getMutationStats().getTotalMutations(),
                    action.getReport().getMutationStats().getUndetected(),
                    100 - action.getReport().getMutationStats().getKillPercent()));
        }
    }

    private void saveJunitBase(List<TestResultAction> testActions, List<TestRes> junitBases) {
        for (TestResultAction action : testActions) {
            //save base Results
            junitBases.add(new TestRes(action.getDisplayName(), action.getResult().getPassCount(), action.getTotalCount(),
                    action.getResult().getFailCount(), action.getResult().getSkipCount()));
        }
    }


    private void saveCocoBase(List<CoverageAction> coverageActions, List<CoCos> cocoBases) {
        for (CoverageAction action : coverageActions) {
            //save base Results
            //cocoBases.add(new TestRes((action.getDisplayName(), new CoCos(action.getDisplayName(), action.getResult());
        }
    }

}
