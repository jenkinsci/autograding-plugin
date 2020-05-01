package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.echarts.Palette;
import edu.hm.hafner.echarts.PieChartModel;
import edu.hm.hafner.echarts.PieData;

import static edu.hm.hafner.echarts.assertions.Assertions.*;

/**
 * Tests the class {@link PercentagePieChart}.
 *
 * @author Andreas Riepl
 * @author Oliver Scholz
 */
class PercentagePieChartTest {
    private static final String GREEN = Palette.GREEN.getNormal();
    private static final String YELLOW = Palette.YELLOW.getNormal();
    private static final String RED = Palette.RED.getNormal();
    private static final String GRAY = Palette.GRAY.getNormal();

    @Test
    void shouldThrowExceptionIfInvalid() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> chart.create(-1))
                .withMessageContaining("Percentage -1");
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> chart.create(101))
                .withMessageContaining("Percentage 101");
    }

    @Test
    void shouldComputeColorToRedLowerBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(0)).hasColors(RED, GRAY)
                .hasData(createFilledTo(0), getNotFilledTo(100));
    }

    @Test
    void shouldComputeColorToRedUpperBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(49)).hasColors(RED, GRAY)
                .hasData(createFilledTo(49), getNotFilledTo(51));
    }

    @Test
    void shouldComputeColorToYellowLowerBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(50)).hasColors(YELLOW, GRAY)
                .hasData(createFilledTo(50), getNotFilledTo(50));
    }

    @Test
    void shouldComputeColorToYellowUpperBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(79)).hasColors(YELLOW, GRAY)
                .hasData(createFilledTo(79), getNotFilledTo(21));
    }

    @Test
    void shouldComputeColorToGreenLowerBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(80)).hasColors(GREEN, GRAY)
                .hasData(createFilledTo(80), getNotFilledTo(20));
    }

    @Test
    void shouldComputeColorToGreenUpperBoundary() {
        PercentagePieChart chart = new PercentagePieChart();

        assertThat(chart.create(100)).hasColors(GREEN, GRAY)
                .hasData(createFilledTo(100), getNotFilledTo(0));
    }

    private PieData getNotFilledTo(final int percentage) {
        return createPieData(percentage, "NotFilled");
    }

    private PieData createFilledTo(final int percentage) {
        return createPieData(percentage, "Filled");
    }

    private PieData createPieData(final int percentage, final String filled) {
        return new PieData(filled, percentage);
    }
}
