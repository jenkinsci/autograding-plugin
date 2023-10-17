package io.jenkins.plugins.grading;

import java.util.List;
import java.util.stream.Collectors;

import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.grading.AnalysisConfiguration;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.grading.AnalysisScore.AnalysisScoreBuilder;
import edu.hm.hafner.grading.AnalysisSupplier;

import hudson.model.Run;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.analysis.core.model.ResultAction;

/**
 * Supplies {@link AnalysisScore static analysis scores} based on the results of the registered {@link ResultAction}
 * instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsAnalysisSupplier extends AnalysisSupplier {
    private final Run<?, ?> run;

    JenkinsAnalysisSupplier(final Run<?, ?> run) {
        super();

        this.run = run;
    }

    @Override
    protected List<AnalysisScore> createScores(final AnalysisConfiguration configuration) {
        return run.getActions(ResultAction.class).stream().map(action ->
                new AnalysisScoreBuilder().withConfiguration(configuration)
                        .withId(action.getResult().getId())
                        .withDisplayName(action.getLabelProvider().getName())
                        .withTotalErrorsSize(action.getResult().getTotalErrorsSize())
                        .withTotalHighSeveritySize(action.getResult().getTotalHighPrioritySize())
                        .withTotalNormalSeveritySize(action.getResult().getTotalNormalPrioritySize())
                        .withTotalLowSeveritySize(action.getResult().getTotalLowPrioritySize())
                        .build()).collect(Collectors.toList());
    }

    List<Report> getReports() {
        return run.getActions(ResultAction.class)
                .stream()
                .map(ResultAction::getResult)
                .map(AnalysisResult::getIssues)
                .collect(Collectors.toList());
    }
}
