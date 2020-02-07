package io.jenkins.plugins.quality.core;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import javax.annotation.Nonnull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.tasks.junit.TestResultAction;
import io.jenkins.plugins.coverage.CoverageAction;
import io.jenkins.plugins.coverage.targets.CoverageElement;
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

    private List<DefaultChecks> createDefaultBase(final List<ResultAction> actions) {
        List<DefaultChecks> defaultBase = new ArrayList<>();
        for (ResultAction action : actions) {
            //store base Results from static checks
            defaultBase.add(new DefaultChecks(action.getId(), action.getResult().getTotalErrorsSize(),
                    action.getResult().getTotalHighPrioritySize(), action.getResult().getTotalNormalPrioritySize(),
                    action.getResult().getTotalLowPrioritySize(), action.getResult().getTotalSize()));
        }
        return defaultBase;
    }

    private List<PITs> createPitBase(final List<PitBuildAction> pitAction) {
        List<PITs> pitBases = new ArrayList<>();
        for (PitBuildAction action : pitAction) {
            //store base Results from mutation check
            pitBases.add(new PITs(action.getDisplayName(), action.getReport().getMutationStats().getTotalMutations(),
                    action.getReport().getMutationStats().getUndetected(),
                    action.getReport().getMutationStats().getKillPercent()));
        }
        return pitBases;
    }

    private List<TestRes> createJunitBase(final List<TestResultAction> testActions) {
        List<TestRes> junitBases = new ArrayList<>();
        for (TestResultAction action : testActions) {
            //store base Results from junit tests
            junitBases.add(new TestRes(action.getDisplayName(), action.getResult().getPassCount(), action.getTotalCount(),
                    action.getResult().getFailCount(), action.getResult().getSkipCount()));
        }
        return junitBases;
    }


    private List<CoCos> createCocoBase(final List<CoverageAction> coverageActions) {
        List<CoCos> cocoBases = new ArrayList<>();
        for (CoverageAction action : coverageActions) {
            //store base Results from code coverage check
            Set<CoverageElement> elements = action.getResult().getElements();
            for (CoverageElement element : elements) {
                cocoBases.add(new CoCos(action.getResult().getName(),
                        (int) action.getResult().getCoverage(element).numerator,
                        (int) action.getResult().getCoverage(element).denominator,
                        action.getResult().getCoverage(element).getPercentage()));
            }
        }
        return cocoBases;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace,
                        @Nonnull final Launcher launcher,
                        @Nonnull final TaskListener listener) {

        listener.getLogger().println("[CodeQuality] Starting extraction of previous performed checks");
        List<ResultAction> actions = run.getActions(ResultAction.class);
        List<PitBuildAction> pitAction = run.getActions(PitBuildAction.class);
        List<TestResultAction> testActions = run.getActions(TestResultAction.class);
        List<CoverageAction> coverageActions = run.getActions(CoverageAction.class);

        //read configs from XML File
        ConfigXmlStream configReader = new ConfigXmlStream();
        Configuration configs = configReader.read(Paths.get(workspace + "\\Config.xml"));
        listener.getLogger().println("[CodeQuality] Read Configs:");
        listener.getLogger().println("[CodeQuality] MaxScore " + configs.getMaxScore());

        Score score = new Score(configs.getMaxScore());
        score.addConfigs(configs);

        //Defaults Rechnen
        List<DefaultChecks> defaultBase = createDefaultBase(actions);
        for (DefaultChecks base : defaultBase) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addDefaultBase(base);
        }

        //PIT lesen und rechnen
        List<PITs> pitBases = createPitBase(pitAction);
        for (PITs base : pitBases) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addPitBase(base);
        }

        //JUNIT lesen und rechnen
        List<TestRes> junitBases = createJunitBase(testActions);
        for (TestRes base : junitBases) {
            base.setTotalChange(base.calculate(configs, base, score, listener));
            score.addJunitBase(base);
        }

        //code-coverage lesen und rechnen
        List<CoCos> cocoBases = createCocoBase(coverageActions);
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
}
