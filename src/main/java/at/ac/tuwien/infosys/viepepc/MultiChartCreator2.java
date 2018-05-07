package at.ac.tuwien.infosys.viepepc;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
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

@Component("MultiChartCreator2")
public class MultiChartCreator2 extends JFreeChartCreator {

    protected JFreeChart createChart(List<ChartDataHolder> chartDataHolderList) {

        List<XYPlot> plots = new ArrayList<>();

        XYPlot subplot = createPlot(chartDataHolderList.get(0).getWorkflowArrivalDataSet(), chartDataHolderList.get(0).getOptimizedVMDataSet(), chartDataHolderList.get(0).getDate(), chartDataHolderList.get(0).getMaxCoreAxisValue(), chartDataHolderList.get(0).getCoreAxisSteps(), true, false, false);
        plots.add(subplot);

        XYPlot subplot2 = createPlot(chartDataHolderList.get(1).getWorkflowArrivalDataSet(), chartDataHolderList.get(1).getOptimizedVMDataSet(), chartDataHolderList.get(1).getDate(), chartDataHolderList.get(1).getMaxCoreAxisValue(), chartDataHolderList.get(1).getCoreAxisSteps(), false, true, false);
        plots.add(subplot2);


        final CombinedRangeXYPlot plot = new CombinedRangeXYPlot(new NumberAxis("# Leased CPU Cores"));
        plot.setGap(10.0);

        for (XYPlot xyPlot : plots) {
            plot.add(xyPlot, 1);
        }
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);


        chart.setBackgroundPaint(Color.white);
        chart.setBorderVisible(false);


        LegendTitle legend = new LegendTitle((XYPlot) (plot.getSubplots().get(0)));
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addLegend(legend);

        return chart;

    }


    protected XYPlot createPlot(XYDataset workflowArrivalDataSet, XYDataset optimizedVMDataSet, Date maxDate, int maxCoreAxisValue, int coreAxisSteps, boolean left, boolean right, boolean time) {


        final DateAxis dateAxis = new DateAxis("Time in Minutes");
        final XYPlot plot = new XYPlot(optimizedVMDataSet, dateAxis, null, new XYLineAndShapeRenderer());

        plot.setBackgroundPaint(Color.white);

        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);


        {   // Process Axis
            plot.setDataset(1, workflowArrivalDataSet);
            plot.mapDatasetToRangeAxis(1, 1);
            final ValueAxis arrivalAxis = new NumberAxis("# Arrived Processes");
            plot.setRangeAxis(1, arrivalAxis);
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(1);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAutoRangeIncludesZero(true);
            rangeAxis.setRange(0, 10);
            NumberTickUnit unit = new NumberTickUnit(2);
            rangeAxis.setTickUnit(unit);
            plot.getRangeAxis(1).setVisible(right);
        }

        {   // Time Axis
            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, false);
            renderer.setSeriesShapesVisible(0, true);

            DateAxis axis = (DateAxis) plot.getDomainAxis();
            axis.setDateFormatOverride(new CustomSimpleDateFormat("mm"));
            axis.setMaximumDate(maxDate);

            plot.setRenderer(1, renderer);
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.red);
            plot.getRendererForDataset(plot.getDataset(1)).setBaseStroke(new BasicStroke(1f));
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesShape(0, ShapeUtilities.createRegularCross(2f, 0.3f));

            plot.getDomainAxis().setVisible(time);
        }

        {   // VM axis
            final ValueAxis axis2 = new NumberAxis("# Leased CPU Cores");
            plot.setRangeAxis(0, axis2);
            final ValueAxis cpuAxis = plot.getRangeAxis(0);
            plot.setRangeAxis(0, cpuAxis);
            final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis(0);
            rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis1.setAutoRangeIncludesZero(true);
            NumberTickUnit unit1 = new NumberTickUnit(coreAxisSteps);
            rangeAxis1.setTickUnit(unit1);
            rangeAxis1.setRange(0, maxCoreAxisValue);

            plot.setDataset(0, optimizedVMDataSet);

            final XYStepRenderer renderer = new XYStepRenderer();
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShapesVisible(0, false);
            plot.setRenderer(0, renderer);

            XYItemRenderer rendererForDataset = plot.getRendererForDataset(plot.getDataset(0));
            rendererForDataset.setSeriesPaint(0, Color.blue);
            rendererForDataset.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{3.0f, 3.0f}, 3.0f));
            rendererForDataset.setSeriesPaint(1, Color.DARK_GRAY);
            rendererForDataset.setSeriesStroke(1, new BasicStroke(2.0f));

            plot.getRangeAxis(0).setVisible(left);
        }

        return plot;
    }



}
