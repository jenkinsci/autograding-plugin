package io.jenkins.plugins.grading;

import java.util.Collections;
import java.util.List;

import edu.hm.hafner.coverage.Coverage;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.grading.PitConfiguration;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.PitScore.PitScoreBuilder;
import edu.hm.hafner.grading.PitSupplier;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

/**
 * Supplies {@link PitScore mutation coverage scores} based on the results of the registered
 * {@link CoverageBuildAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsPitSupplier extends PitSupplier {
    private static final String PIT_DEFAULT_ID = "pit";
    private final Run<?, ?> run;

    JenkinsPitSupplier(final Run<?, ?> run) {
        super();

        this.run = run;
    }

    @Override
    protected List<PitScore> createScores(final PitConfiguration configuration) {
        List<CoverageBuildAction> actions = run.getActions(CoverageBuildAction.class);
        for (CoverageBuildAction action : actions) {
            if (PIT_DEFAULT_ID.equals(action.getUrlName())) {
                var value = action.getValueForMetric(Baseline.PROJECT, Metric.MUTATION);
                if (value.isPresent() && value.get() instanceof Coverage) {
                    var coverage = (Coverage) value.get();
                    PitScore score = new PitScoreBuilder().withConfiguration(configuration)
                            .withDisplayName("Mutations")
                            .withTotalMutations(coverage.getTotal())
                            .withUndetectedMutations(coverage.getMissed())
                            .build();
                    return Collections.singletonList(score);
                }
            }
        }
        return List.of();
    }
}
