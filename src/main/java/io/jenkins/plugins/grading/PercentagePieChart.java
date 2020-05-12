package io.jenkins.plugins.grading;

import java.util.function.Function;

import edu.hm.hafner.echarts.Palette;
import edu.hm.hafner.echarts.PieChartModel;
import edu.hm.hafner.echarts.PieData;
import edu.hm.hafner.util.Ensure;

/**
 * Builds the model for a pie chart showing a percentage.
 *
 * @author Ullrich Hafner
 */
public class PercentagePieChart {
    /**
     * Creates the chart for the specified percentage. Uses the default color model.
     *
     * @param percentage
     *         the percentage to render
     *
     * @return the chart model
     */
    public PieChartModel create(final int percentage) {
        return createWithPaletteMapper(percentage, this::computeColor);
    }

    /**
     * Creates the chart for the specified percentage.
     *
     * @param percentage
     *         the percentage to render
     * @param colorMapper
     *         maps the percentage to a color {@link Palette} instance
     *
     * @return the chart model
     */
    public PieChartModel createWithPaletteMapper(final int percentage, final Function<Integer, Palette> colorMapper) {
        return createMode(percentage, colorMapper.apply(percentage).getNormal());
    }

    /**
     * Creates the chart for the specified percentage.
     *
     * @param percentage
     *         the percentage to render
     * @param colorMapper
     *         maps the percentage to a color value
     *
     * @return the chart model
     */
    public PieChartModel createWithStringMapper(final int percentage, final Function<Integer, String> colorMapper) {
        return createMode(percentage, colorMapper.apply(percentage));
    }

    private PieChartModel createMode(final int percentage, final String actualColorValue) {
        Ensure.that(percentage < 0 || percentage > 100)
                .isFalse("Percentage %s must be in interval [0,100]", percentage);

        PieChartModel model = new PieChartModel("Percentage");

        model.add(new PieData("Filled", percentage), actualColorValue);
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
