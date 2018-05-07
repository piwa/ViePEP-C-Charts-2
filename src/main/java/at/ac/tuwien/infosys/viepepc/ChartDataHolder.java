package at.ac.tuwien.infosys.viepepc;

import lombok.Getter;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.Date;

@Getter
public class ChartDataHolder {

    private String chartName;
    private TimeSeriesCollection workflowArrivalDataSet;
    private TimeSeriesCollection optimizedVMDataSet;
    private Date date;
    private int maxCoreAxisValue;
    private int coreAxisSteps;


    public ChartDataHolder(String chartName, TimeSeriesCollection workflowArrivalDataSet, TimeSeriesCollection optimizedVMDataSet, Date date, int maxCoreAxisValue, int coreAxisSteps) {

        this.chartName = chartName;
        this.workflowArrivalDataSet = workflowArrivalDataSet;
        this.optimizedVMDataSet = optimizedVMDataSet;
        this.date = date;
        this.maxCoreAxisValue = maxCoreAxisValue;
        this.coreAxisSteps = coreAxisSteps;

    }
}
