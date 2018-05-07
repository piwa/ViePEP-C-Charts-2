package at.ac.tuwien.infosys.viepepc;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("MultiChartCreator")
public class MultiChartCreator extends JFreeChartCreator {

    protected JFreeChart createChart(List<ChartDataHolder> chartDataHolderList) {

        List<XYPlot> plots = new ArrayList<>();
        Date maxDate = null;
        for (ChartDataHolder dataHolder : chartDataHolderList) {


            XYPlot subplot = createPlot(dataHolder.getWorkflowArrivalDataSet(), dataHolder.getOptimizedVMDataSet(), dataHolder.getDate(), dataHolder.getMaxCoreAxisValue(), dataHolder.getCoreAxisSteps());
            maxDate = dataHolder.getDate();


            plots.add(subplot);
        }

        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Time in Minutes"));
        plot.setGap(10.0);

        for (XYPlot xyPlot : plots) {
            plot.add(xyPlot, 1);
        }
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);


        chart.setBackgroundPaint(Color.white);
        chart.setBorderVisible(false);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new CustomSimpleDateFormat("mm"));
        axis.setMaximumDate(maxDate);

        LegendTitle legend = new LegendTitle((XYPlot) (plot.getSubplots().get(0)));
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addLegend(legend);

        return chart;

    }


    protected XYPlot createPlot(XYDataset workflowArrivalDataSet, XYDataset optimizedVMDataSet, Date maxDate, int maxCoreAxisValue, int coreAxisSteps) {


        final ValueAxis cpuAxis = new NumberAxis("# Leased CPU Cores");
        final XYPlot plot = new XYPlot(optimizedVMDataSet, null, cpuAxis, new XYLineAndShapeRenderer());

        plot.setBackgroundPaint(Color.white);

        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);


        {   // Process Axis
            final ValueAxis arrivalAxis = new NumberAxis("# Arrived Processes");
            plot.setDataset(1, workflowArrivalDataSet);
            plot.mapDatasetToRangeAxis(1, 1);
            plot.setRangeAxis(1, arrivalAxis);

            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(1);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAutoRangeIncludesZero(true);
            rangeAxis.setRange(0, 10);
            NumberTickUnit unit = new NumberTickUnit(2);
            rangeAxis.setTickUnit(unit);
        }

        {   // Time Axis
            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, false);
            renderer.setSeriesShapesVisible(0, true);
            plot.setRenderer(1, renderer);
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.red);
            plot.getRendererForDataset(plot.getDataset(1)).setBaseStroke(new BasicStroke(1f));
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesShape(0, ShapeUtilities.createRegularCross(2f, 0.3f));
        }

        {   // VM axis
            plot.setDataset(0, optimizedVMDataSet);
            plot.setRangeAxis(0, cpuAxis);
            final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis(0);
            rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis1.setAutoRangeIncludesZero(true);
            NumberTickUnit unit1 = new NumberTickUnit(coreAxisSteps);
            rangeAxis1.setTickUnit(unit1);
            rangeAxis1.setRange(0, maxCoreAxisValue);

            final XYStepRenderer renderer = new XYStepRenderer();
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShapesVisible(0, false);
            plot.setRenderer(0, renderer);

            XYItemRenderer rendererForDataset = plot.getRendererForDataset(plot.getDataset(0));
            rendererForDataset.setSeriesPaint(0, Color.blue);
            rendererForDataset.setSeriesStroke(0, new BasicStroke(2.0f));
            rendererForDataset.setSeriesPaint(1, Color.DARK_GRAY);
            rendererForDataset.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{3.0f, 3.0f}, 6.0f));
        }

        return plot;
    }

}
