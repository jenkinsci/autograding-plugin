package io.jenkins.plugins.grading;

import edu.hm.hafner.echarts.Palette;
import edu.hm.hafner.echarts.PieChartModel;
import edu.hm.hafner.echarts.PieData;

/**
 * Builds the model for a pie chart showing a percentage.
 *
 * @author Ullrich Hafner
 */
public class PercentagePieChart {
    /**
     * Creates the chart for the specified percentage.
     *
     * @param percentage
     *         the percentage to render
     *
     * @return the chart model
     */
    // TODO: add validation
    public PieChartModel create(final int percentage) {
        PieChartModel model = new PieChartModel("Percentage");

        Palette color = computeColor(percentage);
        model.add(new PieData("Filled", percentage), color);
        model.add(new PieData("NotFilled", 100 - percentage), Palette.GRAY);

        return model;
    }

    private Palette computeColor(final int percentage) {
        if (percentage < 50) {
            return Palette.RED;
        }
        else if (percentage < 80) {
            return Palette.YELLOW;
        }
        else {
            return Palette.GREEN;
        }
    }
}
