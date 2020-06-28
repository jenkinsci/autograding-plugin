package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.hm.hafner.grading.CoverageConfiguration;
import edu.hm.hafner.grading.CoverageScore;
import edu.hm.hafner.grading.CoverageScore.CoverageScoreBuilder;
import edu.hm.hafner.grading.CoverageSupplier;

import hudson.model.Run;

import io.jenkins.plugins.coverage.CoverageAction;
import io.jenkins.plugins.coverage.targets.CoverageElement;

/**
 * Supplies {@link CoverageScore coverage scores} based on the results of the registered
 * {@link CoverageAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageSupplier extends CoverageSupplier {
    private final Run<?, ?> run;

    JenkinsCoverageSupplier(final Run<?, ?> run) {
        this.run = run;
    }

    @Override
    protected List<CoverageScore> createScores(final CoverageConfiguration configuration) {
        List<CoverageScore> scores = new ArrayList<>();
        CoverageAction action = run.getAction(CoverageAction.class);
        if (action != null) {
            scores.add(createCoverageScore(action, CoverageElement.LINE).withConfiguration(configuration).build());
            scores.add(createCoverageScore(action, CoverageElement.CONDITIONAL).withConfiguration(configuration).build());
        }
        return scores;
    }

    private CoverageScoreBuilder createCoverageScore(final CoverageAction action, final CoverageElement type) {
        return new CoverageScoreBuilder().withId(StringUtils.lowerCase(type.getName()))
                .withDisplayName(type.getName() + " Coverage")
                .withCoveredPercentage(action.getResult().getCoverage(type).getPercentage());
    }
}
