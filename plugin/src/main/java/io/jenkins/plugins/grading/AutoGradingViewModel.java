package io.jenkins.plugins.grading;

import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.PercentagePieChart;
import edu.hm.hafner.grading.AggregatedScore;
import edu.umd.cs.findbugs.annotations.CheckForNull;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import hudson.model.ModelObject;
import hudson.model.Run;

import io.jenkins.plugins.bootstrap5.MessagesViewModel;
import io.jenkins.plugins.echarts.JenkinsPalette;

/**
 * Server side model that provides the data for the details view of the autograding results. The layout of the
 * associated view is defined in the corresponding jelly view 'index.jelly'.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGradingViewModel implements ModelObject {
    private static final JacksonFacade JACKSON_FACADE = new JacksonFacade();
    private static final String INFO_MESSAGES_VIEW_URL = "info";
    private static final int NOT_SUFFICIENT_PERCENTAGE = 50;
    private static final int EXCELLENT_PERCENTAGE = 80;

    private final Run<?, ?> owner;
    private final AggregatedScore score;

    /**
     * Creates a new instance of {@link AutoGradingViewModel}.
     *
     * @param owner
     *         the build as owner of this view
     * @param score
     *         the scores to show in the view
     */
    AutoGradingViewModel(final Run<?, ?> owner, final AggregatedScore score) {
        super();

        this.owner = owner;
        this.score = score;
    }

    public Run<?, ?> getOwner() {
        return owner;
    }

    @Override
    public String getDisplayName() {
        return Messages.Action_Name();
    }

    public AggregatedScore getScore() {
        return score;
    }

    /**
     * Returns the UI model for an ECharts progress chart.
     *
     * @param percentage
     *         the percentage to show
     *
     * @return the UI model as JSON
     */
    @SuppressWarnings("unused") // Called by jelly view
    public String getProgressModel(final int percentage) {
        return JACKSON_FACADE.toJson(new PercentagePieChart()
                .createWithStringMapper(percentage, this::mapColors));
    }

    private String mapColors(final int percentage) {
        if (percentage < NOT_SUFFICIENT_PERCENTAGE) {
            return JenkinsPalette.RED.normal();
        }
        else if (percentage < EXCELLENT_PERCENTAGE) {
            return JenkinsPalette.ORANGE.normal();
        }
        else {
            return JenkinsPalette.GREEN.normal();
        }
    }

    /**
     * Returns a new subpage for the selected link.
     *
     * @param link
     *         the link to identify the subpage to show
     * @param request
     *         Stapler request
     * @param response
     *         Stapler response
     *
     * @return the new subpage
     */
    @SuppressWarnings("unused") // Called by jelly view
    @CheckForNull
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {
        if (INFO_MESSAGES_VIEW_URL.equals(link)) {
            return new MessagesViewModel(getOwner(), Messages.Action_Name(),
                    score.getInfoMessages(), score.getErrorMessages());
        }
        return null; // fallback on any other URL
    }
}
