package io.jenkins.plugins.grading;

import java.nio.file.Paths;
import java.util.*;

import edu.hm.hafner.util.VisibleForTesting;
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
 * This recorder gathers all the needed results of previous run check in the job.
 * Inputs are Saved, and Quality Score is computed on base of defined configurations.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGrader extends Recorder implements SimpleBuildStep {
    /**
     * Creates a new instance of {@link  AutoGrader}.
     */
    @DataBoundConstructor
    public AutoGrader() {
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
        Configuration configs = configReader.read(Paths.get(workspace.child("auto-grading.xml").getRemote()));
        listener.getLogger().println("[CodeQuality] Read Configs:");
        listener.getLogger().println("[CodeQuality] Configs read successfully.");

        Score score = new Score(configs.getMaxScore());
        score.addConfigs(configs);

        //Defaults Rechnen
        if (actions.isEmpty()) {
            score.addToScore(-configs.getdMaxScore());
        }
        else {
            List<DefaultChecks> defaultBase = createDefaultBase(actions);
            updateDefaultGrade(configs, score, defaultBase, listener);
        }

        //PIT lesen und rechnen
        if (pitAction.isEmpty()) {
            score.addToScore(-configs.getpMaxScore());
        }
        else {
            List<PITs> pitBases = createPitBase(pitAction);
            updatePitGrade(configs, score, pitBases, listener);
        }

        //JUNIT lesen und rechnen
        if (testActions.isEmpty()) {
            score.addToScore(-configs.getjMaxScore());
        }
        else {
            List<TestRes> junitBases = createJunitBase(testActions);
            updateJunitGrade(configs, score, junitBases, listener);
        }

        //code-coverage lesen und rechnen
        if (coverageActions.isEmpty()) {
            score.addToScore(-configs.getcMaxScore());
        }
        else {
            List<CoCos> cocoBases = createCocoBase(coverageActions);
            updateCocoGrade(configs, score, cocoBases, listener);
        }

        listener.getLogger().println("[CodeQuality] Code Quality Score calculation completed");

        run.addAction(new AutoGradingBuildAction(run, score));
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
        if (configs.getjMaxScore() + change >= 0 && change < 0) {
            score.addToScore(change);
            listener.getLogger().println("[CodeQuality] Updated Score by junit Delta");
        }
        else if (configs.getjMaxScore() + change < 0) {
            score.addToScore(-configs.getjMaxScore());
            listener.getLogger().println("[CodeQuality] Updated Score by junit Delta");
        }

    }

    @VisibleForTesting
    void updateCocoGrade(final Configuration configs, final List<CoCos> cocoBases, final Score score) {
        updateCocoGrade(configs, score, cocoBases, TaskListener.NULL);
    }

    @VisibleForTesting
    void updateDefaultGrade(final Configuration configs, final Score score,
                            final List<DefaultChecks> defaultBase) {
        updateDefaultGrade(configs, score, defaultBase, TaskListener.NULL);
    }

    @VisibleForTesting
    void updatePitGrade(final Configuration configs, final Score score,
                        final List<PITs> pitBases) {
        updatePitGrade(configs, score, pitBases, TaskListener.NULL);
    }

    @VisibleForTesting
    void updateJunitGrade(final Configuration configs, final Score score, final List<TestRes> junitBases) {
        updateJunitGrade(configs, score, junitBases, TaskListener.NULL);
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
