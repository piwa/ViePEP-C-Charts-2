package at.ac.tuwien.infosys.viepepc;

import at.ac.tuwien.infosys.viepepc.database.entities.ContainerActionsDTO;
import at.ac.tuwien.infosys.viepepc.database.entities.WorkflowDTO;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Philipp Hoenisch on 9/1/14.
 */
@Component("jFreeChartCreator")
public class JFreeChartCreator {

    public void writeAsPDF(JFreeChart chart, OutputStream out, int width, int height) {
        try {
            Rectangle pagesize = new Rectangle(width, height);
            Document document = new Document(pagesize, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
            Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
            chart.draw(g2, r2D);
            g2.dispose();
            cb.addTemplate(tp, 0, 0);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a chart.
     *
     * @param name
     * @param dataset1 the data for the chart.
     * @param maxDate
     * @return a chart.
     */
    protected JFreeChart createChart(String name, final XYDataset dataset3, final XYDataset dataset1, Date maxDate, int maxCoreAxisValue, int coreAxisSteps) {

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(null,"Time in Minutes","# Leased CPU Cores", dataset1,true,false, false );

        chart.setBackgroundPaint(Color.white);
        chart.setBorderVisible(false);

        final XYPlot plot = chart.getXYPlot();


        createPlot(dataset3, maxDate, maxCoreAxisValue, coreAxisSteps, plot);

        Font defaultFont = plot.getDomainAxis().getLabelFont();
        defaultFont = new Font("Arial", Font.PLAIN, 15);
        chart.getLegend().setItemFont(defaultFont);
        XYTitleAnnotation ta = new XYTitleAnnotation(1, 1, chart.getLegend(), RectangleAnchor.TOP_RIGHT);

//        ta.setMaxWidth(1);
        plot.addAnnotation(ta);
        chart.removeLegend();

        return chart;

    }

    protected XYPlot createPlot(XYDataset dataset3, Date maxDate, int maxCoreAxisValue, int coreAxisSteps, XYPlot plot) {
        plot.setBackgroundPaint(Color.white);

        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        Font defaultFont = plot.getDomainAxis().getLabelFont();
        defaultFont = new Font("Arial", Font.PLAIN, 15);//defaultFont.deriveFont(13f);


        {   // Process Axis
            plot.setDataset(1, dataset3);
            plot.mapDatasetToRangeAxis(1, 1);
            final ValueAxis axis2 = new NumberAxis("# Arrived Processe Requests");
            axis2.setLabelFont(defaultFont);
            axis2.setTickLabelFont(defaultFont);
            plot.setRangeAxis(1, axis2);

            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(1);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAutoRangeIncludesZero(true);
            rangeAxis.setRange(0, 10);
            NumberTickUnit unit = new NumberTickUnit(2);
            rangeAxis.setTickUnit(unit);
        }

        {   // Time Axis
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            axis.setLabelFont(defaultFont);
            axis.setTickLabelFont(defaultFont);
            axis.setDateFormatOverride(new CustomSimpleDateFormat("mm"));
            axis.setMaximumDate(maxDate);
            axis.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE, 40));

            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, false);
            renderer.setSeriesShapesVisible(0, true);
            plot.setRenderer(1, renderer);
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.red);
            plot.getRendererForDataset(plot.getDataset(1)).setBaseStroke(new BasicStroke(2f));
            plot.getRendererForDataset(plot.getDataset(1)).setSeriesShape(0, ShapeUtilities.createRegularCross(2f, 0.2f));
        }

        {   // VM axis
            final ValueAxis axis2 = new NumberAxis("# Leased CPU Cores");
            axis2.setLabelFont(defaultFont);
            axis2.setTickLabelFont(defaultFont);
            plot.setRangeAxis(0, axis2);
            final NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis(0);
            rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis1.setAutoRangeIncludesZero(true);
            NumberTickUnit unit1 = new NumberTickUnit(coreAxisSteps);
            rangeAxis1.setTickUnit(unit1);
            rangeAxis1.setRange(0, maxCoreAxisValue);
            rangeAxis1.setLabelFont(defaultFont);
            rangeAxis1.setTickLabelFont(defaultFont);

            final XYStepRenderer renderer = new XYStepRenderer();
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShapesVisible(0, false);
            plot.setRenderer(0, renderer);

            XYItemRenderer rendererForDataset = plot.getRendererForDataset(plot.getDataset(0));
            rendererForDataset.setSeriesPaint(0, Color.blue);
            rendererForDataset.setSeriesStroke(0, new BasicStroke(2.0f));
            rendererForDataset.setSeriesPaint(1, Color.DARK_GRAY);
            rendererForDataset.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4.0f}, 1.0f));
        }

//        plot.getDomainAxis().setLabelFont( plot.getRangeAxis(0).getLabelFont() );

        return plot;
    }

    protected class CustomSimpleDateFormat extends SimpleDateFormat {
        public CustomSimpleDateFormat(String m) {
            super(m);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer format = super.format(date, toAppendTo, pos);
            GregorianCalendar greg = new GregorianCalendar();
            greg.setTime(date);

            int hour = greg.get(Calendar.HOUR_OF_DAY);
            int minute = greg.get(Calendar.MINUTE);
            int currentMinuteOfDay = ((hour - 9) * 60) + minute;
            format = new StringBuffer(String.valueOf(currentMinuteOfDay));

            return format;
        }

        @Override
        public Date parse(String text, ParsePosition pos) {
            return super.parse(text, pos);
        }

    }

    public TimeSeriesCollection createArrivalDataSet(List<WorkflowDTO> results) {
        final TimeSeries series1 = new TimeSeries("Process Arrivals");
        SortedMap<Date, List<WorkflowDTO>> arrivalSorted = new TreeMap<>();
        for (WorkflowDTO result : results) {
            Date arrivedAt = result.getArrivedAt();
            List<WorkflowDTO> workflowDTOs = arrivalSorted.get(arrivedAt);
            if (workflowDTOs == null) {
                workflowDTOs = new ArrayList<>();
            }
            workflowDTOs.add(result);
            arrivalSorted.put(arrivedAt, workflowDTOs);
        }
        int x = 0;
        Date min = null;
        for (Date date : arrivalSorted.keySet()) {
            if (min == null) {
                min = date;
            }
            List<WorkflowDTO> workflowDTOs = arrivalSorted.get(date);
            date = new Date(date.getTime() - min.getTime());

            RegularTimePeriod period = new Minute(date);
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(period, workflowDTOs.size());
            series1.addOrUpdate(timeSeriesDataItem);
            x++;
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);

        return dataset;

    }

    public TimeSeriesCollection createContainerDataSet(String setName, List<ContainerActionsDTO> evaluation1, List<ContainerActionsDTO> evaluation2, List<ContainerActionsDTO> evaluation3) {
        final TimeSeries averages = new TimeSeries(setName);
        final TimeSeries series1 = new TimeSeries("# Leased CPU Cores 1");
        final TimeSeries series2 = new TimeSeries("# Leased CPU Cores 2");
        final TimeSeries series3 = new TimeSeries("# Leased CPU Cores 3");

        ContainerActionsDTO first = null;
        ContainerActionsDTO last = null;

        first = getFirst(evaluation3, getFirst(evaluation2, getFirst(evaluation1, null)));
        last = getLast(evaluation3, getLast(evaluation2, getLast(evaluation1, null)));


        Calendar dateStart = new GregorianCalendar();
        dateStart.setTime(first.getDate());
        dateStart.set(Calendar.MINUTE, 0);

        Calendar dateEnd = new GregorianCalendar();
        dateEnd.setTime(last.getDate());

        Map<Integer, List<ContainerActionsDTO>> values = new TreeMap<>();
        long maxMinutes = TimeUnit.MILLISECONDS.toMinutes(dateEnd.getTimeInMillis() - dateStart.getTimeInMillis());

//        maxMinutes = (long) (Math.ceil(maxMinutes / 5.0) * 5);

        for (int i = 0; i <= maxMinutes; i++) {
            values.put(i, new ArrayList<ContainerActionsDTO>());
        }

        for (ContainerActionsDTO containerActionsDTO : evaluation1) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(containerActionsDTO.getDate().getTime() - dateStart.getTimeInMillis());
            if (values.get(minutes) != null) {
                values.get(minutes).add(containerActionsDTO);
            }
        }
        for (ContainerActionsDTO containerActionsDTO : evaluation2) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(containerActionsDTO.getDate().getTime() - dateStart.getTimeInMillis());
            if (values.get(minutes) != null) {
                values.get(minutes).add(containerActionsDTO);
            }
        }
        for (ContainerActionsDTO containerActionsDTO : evaluation3) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(containerActionsDTO.getDate().getTime() - dateStart.getTimeInMillis());
            if (values.get(minutes) != null) {
                values.get(minutes).add(containerActionsDTO);
            }
        }


        double lastAverage = 0;
        for (Integer minute : values.keySet()) {
            String output = "" + minute + " ";
            double sum = 0;
            Date date = null;
            for (ContainerActionsDTO dto : values.get(minute)) {
                output += " " + dto.getCoreAmount();
                sum += dto.getCoreAmount();
                date = dto.getDate();
            }
//            System.out.println(output + " avg: " + sum / 3.0);

            RegularTimePeriod period = new Millisecond(date);
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(period, sum / 3.0);
            averages.add(timeSeriesDataItem);
        }


        for (ContainerActionsDTO containerActionsDTO : evaluation1) {
            RegularTimePeriod period = new Minute(containerActionsDTO.getDate());
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(period, containerActionsDTO.getCoreAmount());
            series1.add(timeSeriesDataItem);
        }
        for (ContainerActionsDTO containerActionsDTO : evaluation2) {
            RegularTimePeriod period = new Minute(containerActionsDTO.getDate());
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(period, containerActionsDTO.getCoreAmount());
            series2.add(timeSeriesDataItem);
        }
        for (ContainerActionsDTO containerActionsDTO : evaluation3) {
            RegularTimePeriod period = new Minute(containerActionsDTO.getDate());
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(period, containerActionsDTO.getCoreAmount());
            series3.add(timeSeriesDataItem);
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(averages);
//        dataset.addSeries(series1);
//        dataset.addSeries(series2);
//        dataset.addSeries(series3);
        return dataset;
    }


    private ContainerActionsDTO getFirst(List<ContainerActionsDTO> evaluation1, ContainerActionsDTO first) {
        for (ContainerActionsDTO containerActionsDTO : evaluation1) {
            if (first == null) {
                first = containerActionsDTO;
            } else if (first.getDate().after(containerActionsDTO.getDate())) {
                first = containerActionsDTO;
            }
        }
        return first;
    }

    private ContainerActionsDTO getLast(List<ContainerActionsDTO> evaluation, ContainerActionsDTO last) {
        for (ContainerActionsDTO containerActionsDTO : evaluation) {
            if (last == null) {
                last = containerActionsDTO;
            } else if (last.getDate().before(containerActionsDTO.getDate())) {
                last = containerActionsDTO;
            }
        }
        return last;
    }


}
