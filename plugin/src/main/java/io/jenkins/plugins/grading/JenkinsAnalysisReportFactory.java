package io.jenkins.plugins.grading;

import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.grading.AggregatedScore.AnalysisReportFactory;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;

import io.jenkins.plugins.analysis.core.model.ResultAction;

/**
 * Supplies {@link AnalysisScore static analysis scores} based on the results of the registered {@link ResultAction}
 * instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsAnalysisReportFactory implements AnalysisReportFactory {
    private final Run<?, ?> run;

    JenkinsAnalysisReportFactory(final Run<?, ?> run) {
        super();

        this.run = run;
    }

    @Override
    public Report create(final ToolConfiguration tool, final FilteredLog log) {
        var result = run.getActions(ResultAction.class).stream()
                .filter(action -> action.getId().equals(tool.getId()))
                .findFirst();
        if (result.isPresent()) {
            var action = result.get();
            var analysisResult = action.getResult();
            log.logInfo("-> Found result action for %s with %d issues",
                    action.getDisplayName(), analysisResult.getTotalSize());
            return analysisResult.getIssues();
        }
        else {
            log.logError("No result action found for ID '%s'", tool.getId());
            return new Report();
        }
    }
}
