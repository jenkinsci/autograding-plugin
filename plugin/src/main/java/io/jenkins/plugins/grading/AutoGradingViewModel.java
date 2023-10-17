package io.jenkins.plugins.grading;

import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.PercentagePieChart;
import edu.hm.hafner.grading.AggregatedScore;

import hudson.model.ModelObject;
import hudson.model.Run;

import io.jenkins.plugins.echarts.JenkinsPalette;

/**
 * Server side model that provides the data for the details view of the autograding results. The layout of the
 * associated view is defined in the corresponding jelly view 'index.jelly'.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGradingViewModel implements ModelObject {
    private static final JacksonFacade JACKSON_FACADE = new JacksonFacade();

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
        if (percentage < 50) {
            return JenkinsPalette.RED.normal();
        }
        else if (percentage < 80) {
            return JenkinsPalette.ORANGE.normal();
        }
        else {
            return JenkinsPalette.GREEN.normal();
        }

    }
}
