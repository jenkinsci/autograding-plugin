package io.jenkins.plugins.grading;

import edu.hm.hafner.coverage.Coverage;
import edu.hm.hafner.coverage.ModuleNode;
import edu.hm.hafner.coverage.Node;
import edu.hm.hafner.grading.AggregatedScore.CoverageReportFactory;
import edu.hm.hafner.grading.ToolConfiguration;
import edu.hm.hafner.util.FilteredLog;

import hudson.model.Run;

import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;

/**
 * Supplies {@link Coverage coverage scores} based on the results of the registered
 * {@link CoverageBuildAction} instances.
 *
 * @author Ullrich Hafner
 */
class JenkinsCoverageReportFactory implements CoverageReportFactory {
    private final Run<?, ?> run;

    JenkinsCoverageReportFactory(final Run<?, ?> run) {
        super();

        this.run = run;
    }

    @Override
    public Node create(final ToolConfiguration tool, final FilteredLog log) {
        var result = run.getActions(CoverageBuildAction.class).stream()
                .filter(action -> action.getUrlName().equals(tool.getId()))
                .findFirst();
        if (result.isPresent()) {
            var action = result.get();
            var coverageResult = action.getResult();
            log.logInfo("-> Found result action for %s: %s",
                    action.getDisplayName(), coverageResult);
            return coverageResult;
        }
        else {
            log.logError("No result action found for ID '%s'", tool.getId());
            return new ModuleNode("empty");
        }
    }
}
