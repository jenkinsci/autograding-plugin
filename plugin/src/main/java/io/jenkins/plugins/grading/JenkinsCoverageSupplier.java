package io.jenkins.plugins.grading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.hm.hafner.coverage.Coverage;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.grading.CoverageConfiguration;
import edu.hm.hafner.grading.CoverageScore;
import edu.hm.hafner.grading.CoverageScore.CoverageScoreBuilder;
import edu.hm.hafner.grading.CoverageSupplier;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

/**
 * Supplies {@link Coverage coverage scores} based on the results of the registered
 * {@link CoverageBuildAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageSupplier extends CoverageSupplier {
    private static final String COVERAGE_DEFAULT_ID = "coverage";
    private final Run<?, ?> run;

    JenkinsCoverageSupplier(final Run<?, ?> run) {
        super();

        this.run = run;
    }

    @Override
    protected List<CoverageScore> createScores(final CoverageConfiguration configuration) {
        List<CoverageScore> scores = new ArrayList<>();
        List<CoverageBuildAction> actions = run.getActions(CoverageBuildAction.class);
        for (CoverageBuildAction action : actions) {
            if (COVERAGE_DEFAULT_ID.equals(action.getUrlName())) {
                scores.addAll(createCoverageScore(action, configuration, Metric.LINE));
                scores.addAll(createCoverageScore(action, configuration, Metric.BRANCH));
            }
        }
        return scores;
    }

    private Collection<CoverageScore> createCoverageScore(final CoverageBuildAction action,
            final CoverageConfiguration configuration, final Metric metric) {
        var value = action.getValueForMetric(Baseline.PROJECT, metric);
        if (value.isPresent() && value.get() instanceof Coverage) {
            var coverage = (Coverage)value.get();
            return Collections.singleton(new CoverageScoreBuilder()
                    .withId(StringUtils.lowerCase(metric.toTagName()))
                    .withDisplayName(action.getFormatter().getLabel(metric) + " Coverage")
                    .withCoveredPercentage((int)coverage.getCoveredPercentage().toDouble())
                    .withConfiguration(configuration).build());
        }
        return List.of();
    }
}
