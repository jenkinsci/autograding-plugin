package io.jenkins.plugins.grading;

import java.util.Collections;
import java.util.List;

import edu.hm.hafner.grading.PitConfiguration;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.PitScore.PitScoreBuilder;
import edu.hm.hafner.grading.PitSupplier;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import hudson.model.Run;

/**
 * Supplies {@link PitScore mutation coverage scores} based on the results of the registered
 * {@link PitBuildAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsPitSupplier extends PitSupplier {
    private final Run<?, ?> run;

    JenkinsPitSupplier(final Run<?, ?> run) {
        this.run = run;
    }

    @Override
    protected List<PitScore> createScores(final PitConfiguration configuration) {
        PitBuildAction action = run.getAction(PitBuildAction.class);
        if (action != null) {
            MutationStats mutationStats = action.getReport().getMutationStats();
            PitScore score = new PitScoreBuilder().withConfiguration(configuration)
                    .withDisplayName(action.getDisplayName())
                    .withTotalMutations(mutationStats.getTotalMutations())
                    .withUndetectedMutations(mutationStats.getUndetected())
                    .build();
            return Collections.singletonList(score);
        }
        return Collections.emptyList();
    }
}
