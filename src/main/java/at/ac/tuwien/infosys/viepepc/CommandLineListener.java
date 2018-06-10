package at.ac.tuwien.infosys.viepepc;

import jdk.management.resource.internal.inst.DatagramDispatcherRMHooks;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 20/06/16. edited by gerta sheganaku
 */
@Component
@Slf4j
@Profile("!test")
public class CommandLineListener implements CommandLineRunner {
    @Autowired
    private DataLoader dataLoader;
    @Autowired
    @Qualifier("MultiChartCreator2")
    private MultiChartCreator2 multiChartCreator;

    private List<ChartDataHolder> dataHolderList = new ArrayList<>();


    public void run(String... args) {

        int timeAxisMax = 9500;
        int maxCoreAxisValue = 80;
        int coreAxisSteps = 10;


//        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival - Lenient", "Constant_OnlyContainerGeneticAlgorithm_L_0706_%s", "Constant_OnlyContainerBaseline_L_0706_%s", "Constant_Lenient.pdf", "1", "2", "1", 135, 145);
        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival - Strict", "Constant_OnlyContainerGeneticAlgorithm_S_0706_%s", "Constant_OnlyContainerBaseline_S_0706_%s", "Constant_Strict.pdf", "1", "2", "3", 135, 145);
//
//        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival - Lenient", "Pyramid_OnlyContainerGeneticAlgorithm_L_0706_%s", "Pyramid_OnlyContainerBaseline_L_0706_%s", "Pyramid_Lenient.pdf", "1", "2", "1", 150, 160);
//        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival - Strict", "Pyramid_OnlyContainerGeneticAlgorithm_S_0706_%s", "Pyramid_OnlyContainerBaseline_S_0706_%s", "Pyramid_Strict.pdf", "1", "2", "1", 150, 160);
//


//        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Dev Constant Arrival - Strict", "Develop_OnlyContainerGeneticAlgorithm_1", "Develop_OnlyContainerBaseline_1", "Dev_Constant_Strict.pdf", "1", "1", "1");
//        dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival - Strict", "Constant_OnlyContainerGeneticAlgorithm_S_0906_%s", "Constant_OnlyContainerBaseline_S_0906_%s", "Constant_Strict.pdf", "1", "1", "1");
//        try {
//            dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival - Very Strict", "Constant_OnlyContainerGeneticAlgorithm_VS_0906_%s", "Constant_OnlyContainerBaseline_VS_0906_%s", "Constant_VStrict.pdf", "1", "2", "3");
//        }catch(Exception ex) {}
//        try {
//            dataLoader.createGraphMain(timeAxisMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival - Very Strict", "Pyramid_OnlyContainerGeneticAlgorithm_VS_0906_%s", "Pyramid_OnlyContainerBaseline_VS_0906_%s", "Pyramid_VStrict.pdf", "1", "2", "3");
//        }catch(Exception ex) {}
//
        log.info("Goodbye!");

        System.exit(0);
    }


}

