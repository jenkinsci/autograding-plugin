package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.echarts.Palette;
import edu.hm.hafner.echarts.PieChartModel;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests the class {@link PercentagePieChart}.
 *
 * @author Andreas Riepl
 * @author Oliver Scholz
 *
 */
class PercentagePieChartTest {

    @Test
    void shouldComputeColorToRed() {
        PercentagePieChart chart = new PercentagePieChart();
        PieChartModel model = chart.create(49);

        assertThat(model.getColors().get(0), notNullValue());
        assertThat(model.getColors().get(0), is(Palette.RED.getNormal()));

        assertThat(model.getData().get(0).getName(), is("Filled"));
        assertThat(model.getData().get(0).getValue(), is(49));

        assertThat(model.getColors().get(1), notNullValue());
        assertThat(model.getColors().get(1), is(Palette.GRAY.getNormal()));

        assertThat(model.getData().get(1).getName(), is("NotFilled"));
        assertThat(model.getData().get(1).getValue(), is(51));
    }

    @Test
    void shouldComputeColorToYellowLowerBoundary() {
        PercentagePieChart chart = new PercentagePieChart();
        PieChartModel model = chart.create(50);

        assertThat(model.getColors().get(0), notNullValue());
        assertThat(model.getColors().get(0), is(Palette.YELLOW.getNormal()));

        assertThat(model.getData().get(0).getName(), is("Filled"));
        assertThat(model.getData().get(0).getValue(), is(50));

        assertThat(model.getColors().get(1), notNullValue());
        assertThat(model.getColors().get(1), is(Palette.GRAY.getNormal()));

        assertThat(model.getData().get(1).getName(), is("NotFilled"));
        assertThat(model.getData().get(1).getValue(), is(50));

    }

    @Test
    void shouldComputeColorToYellowUpperBoundary() {
        PercentagePieChart chart = new PercentagePieChart();
        PieChartModel model = chart.create(79);

        assertThat(model.getColors().get(0), notNullValue());
        assertThat(model.getColors().get(0), is(Palette.YELLOW.getNormal()));

        assertThat(model.getData().get(0).getName(), is("Filled"));
        assertThat(model.getData().get(0).getValue(), is(79));

        assertThat(model.getColors().get(1), notNullValue());
        assertThat(model.getColors().get(1), is(Palette.GRAY.getNormal()));

        assertThat(model.getData().get(1).getName(), is("NotFilled"));
        assertThat(model.getData().get(1).getValue(), is(21));

    }

    @Test
    void shouldComputeColorToGreen() {
        PercentagePieChart chart = new PercentagePieChart();
        PieChartModel model = chart.create(80);

        assertThat(model.getColors().get(0), notNullValue());
        assertThat(model.getColors().get(0), is(Palette.GREEN.getNormal()));

        assertThat(model.getData().get(0).getName(), is("Filled"));
        assertThat(model.getData().get(0).getValue(), is(80));

        assertThat(model.getColors().get(1), notNullValue());
        assertThat(model.getColors().get(1), is(Palette.GRAY.getNormal()));

        assertThat(model.getData().get(1).getName(), is("NotFilled"));
        assertThat(model.getData().get(1).getValue(), is(20));
    }
}
