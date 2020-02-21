package io.jenkins.plugins.quality.core;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

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
                cocoBases.add(new CoCos(element.getName(),
                        (int) action.getResult().getCoverage(element).numerator,
                        (int) action.getResult().getCoverage(element).denominator,
                        action.getResult().getCoverage(element).getPercentage()));
            }
        }
        return cocoBases;
    }

    @Override
    public void perform(@NonNull final Run<?, ?> run, @NonNull final FilePath workspace,
                        @NonNull final Launcher launcher,
                        @NonNull final TaskListener listener) {

        listener.getLogger().println("[CodeQuality] Starting extraction of previous performed checks");
        List<ResultAction> actions = run.getActions(ResultAction.class);
        List<PitBuildAction> pitAction = run.getActions(PitBuildAction.class);
        List<TestResultAction> testActions = run.getActions(TestResultAction.class);
        List<CoverageAction> coverageActions = run.getActions(CoverageAction.class);

        //read configs from XML File
        ConfigXmlStream configReader = new ConfigXmlStream();
        Configuration configs = configReader.read(Paths.get(workspace + "\\Config.xml"));
        listener.getLogger().println("[CodeQuality] Read Configs:");
        listener.getLogger().println("[CodeQuality] Configs read successfully.");

        Score score = new Score(configs.getMaxScore());
        score.addConfigs(configs);

        //Defaults Rechnen
        if (!actions.isEmpty()) {
            List<DefaultChecks> defaultBase = createDefaultBase(actions);
            updateDefaultGrade(configs, score, defaultBase, listener);
        } else {
            score.addToScore(-configs.getdMaxScore());
        }

        //PIT lesen und rechnen
        if (!pitAction.isEmpty()) {
            List<PITs> pitBases = createPitBase(pitAction);
            updatePitGrade(configs, score, pitBases, listener);
        } else {
            score.addToScore(-configs.getpMaxScore());
        }

        //JUNIT lesen und rechnen
        if (!testActions.isEmpty()) {
            List<TestRes> junitBases = createJunitBase(testActions);
            updateJunitGrade(configs, score, junitBases, listener);
        } else {
            score.addToScore(-configs.getjMaxScore());
        }

        //code-coverage lesen und rechnen
        if (!coverageActions.isEmpty()) {
            List<CoCos> cocoBases = createCocoBase(coverageActions);
            updateCocoGrade(configs, score, cocoBases, listener);
        } else {
            score.addToScore(-configs.getcMaxScore());
        }

        listener.getLogger().println("[CodeQuality] Code Quality Score calculation completed");

        run.addAction(new ScoreBuildAction(run, score));
    }

    private void updateCocoGrade(final Configuration configs, final Score score, final List<CoCos> cocoBases, @NonNull final TaskListener listener) {
        int change = 0;
        for (CoCos base : cocoBases) {
            change = change + base.calculate(configs, listener);
            score.addCocoBase(base);
            listener.getLogger().println("[CodeQuality] Saved Code Coverage Base Results");
        }

        if (configs.getcMaxScore() + change >= 0 && change < 0) {
            score.addToScore(change);
            listener.getLogger().println("[CodeQuality] Updated Score by Code Coverage Delta");
        }
        else if (configs.getcMaxScore() + change < 0) {
            score.addToScore(-configs.getcMaxScore());
            listener.getLogger().println("[CodeQuality] Updated Score by Code Coverage Delta");
        }


    }

    private void updateDefaultGrade(final Configuration configs, final Score score,
                                    final List<DefaultChecks> defaultBase, @NonNull final TaskListener listener) {
        int change = 0;
        for (DefaultChecks base : defaultBase) {
            change = change + base.calculate(configs, listener);
            score.addDefaultBase(base);
            listener.getLogger().println("[CodeQuality] Saved Static Analysis Base Results");
        }

        if (configs.getdMaxScore() + change >= 0 && change < 0) {
            score.addToScore(change);
            listener.getLogger().println("[CodeQuality] Updated Score by Static Analysis Delta");
        }
        else if (configs.getdMaxScore() + change < 0) {
            score.addToScore(-configs.getdMaxScore());
            listener.getLogger().println("[CodeQuality] Updated Score by Static Analysis Delta");
        }

    }

    private void updatePitGrade(final Configuration configs, final Score score,
                                final List<PITs> pitBases, final @NonNull TaskListener listener) {
        int change = 0;
        for (PITs base : pitBases) {
            change = change + base.calculate(configs, listener);
            score.addPitBase(base);
            listener.getLogger().println("[CodeQuality] Saved pitmuation Base Results");
        }
        if (configs.getpMaxScore() + change >= 0 && change < 0) {
            score.addToScore(change);
            listener.getLogger().println("[CodeQuality] Updated Score by pitmutation Delta");
        }
        else if (configs.getpMaxScore() + change < 0) {
            score.addToScore(-configs.getpMaxScore());
            listener.getLogger().println("[CodeQuality] Updated Score by pitmutation Delta");
        }

    }

    private void updateJunitGrade(final Configuration configs, final Score score, final List<TestRes> junitBases, @NonNull final TaskListener listener) {
        int change = 0;
        for (TestRes base : junitBases) {
            change = change + base.calculate(configs, listener);
            score.addJunitBase(base);
            listener.getLogger().println("[CodeQuality] Saved Junit Base Results");
        }
        if (configs.getjMaxScore() + change <= 0 && change < 0) {
            score.addToScore(change);
            listener.getLogger().println("[CodeQuality] Updated Score by junit Delta");
        }
        else if (configs.getjMaxScore() + change < 0) {
            score.addToScore(-configs.getjMaxScore());
            listener.getLogger().println("[CodeQuality] Updated Score by junit Delta");
        }

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
