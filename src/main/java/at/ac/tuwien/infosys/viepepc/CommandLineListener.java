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

        int predefinedMax = 20;
        int maxCoreAxisValue = 20;
        int coreAxisSteps = 2;

        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival GA - Strict", "develop_OnlyContainerGeneticAlgorithm_%s", "develop_OnlyContainerBaseline_%s", "develop_OnlyContainerGeneticAlgorithm.pdf", "1", "1", "1");

//        constant_lenient(predefinedMax, maxCoreAxisValue, coreAxisSteps);
//        constant_strict(predefinedMax, maxCoreAxisValue, coreAxisSteps);
//        pyramid_lenient(predefinedMax, maxCoreAxisValue, coreAxisSteps);
//        pyramid_strict(predefinedMax, maxCoreAxisValue, coreAxisSteps);


        log.info("Goodbye!");

        System.exit(0);
    }

    private void pyramid_strict(int predefinedMax, int maxCoreAxisValue, int coreAxisSteps) {
        // Pyramid Arrival OneVMforAll - Strict
        dataLoader.createGraphMain(340, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival OneVMforAllContainer - Strict", "Pyramid_OneVMforAllContainer_Strict_1409_%s", "Pyramid_OneVMforAll_Strict_1409_%s", "Pyramid_OneVMforAll_Strict.pdf", "1", "2", "3");

        // Pyramid Arrival OneVMPerTask - Strict
        dataLoader.createGraphMain(140, 20, 260, "Pyramid Arrival OneVMPerTaskContainer - Strict", "Pyramid_OneVMPerTaskContainer_Strict_1409_%s", "Pyramid_OneVMPerTask_Strict_1409_%s", "Pyramid_OneVMPerTask_Strict.pdf", "1", "2", "3");

        // Pyramid Arrival StartParExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival StartParExceedContainer - Strict", "Pyramid_StartParExceedContainer_Strict_1409_%s", "Pyramid_StartParExceed_Strict_1409_%s", "Pyramid_StartParExceed_Strict.pdf", "1", "2", "3");

        // Pyramid Arrival StartParNotExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival StartParNotExceedContainer - Strict", "Pyramid_StartParNotExceedContainer_Strict_1409_%s", "Pyramid_StartParNotExceed_Strict_1409_%s", "Pyramid_StartParNotExceed_Strict.pdf", "1", "2", "3");

        // Pyramid Arrival AllParExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival AllParExceedContainer - Strict", "Pyramid_AllParExceedContainer_Strict_1409_%s", "Pyramid_AllParExceed_Strict_1409_%s", "Pyramid_AllParExceed_Strict.pdf", "1", "2", "3");

        // Pyramid Arrival AllParNotExceed - Strict
        dataLoader.createGraphMain(130, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival AllParNotExceedContainer - Strict", "Pyramid_AllParNotExceedContainer_Strict_1409_%s", "Pyramid_AllParNotExceed_Strict_1409_%s", "Pyramid_AllParNotExceed_Strict.pdf", "1", "2", "3");
    }

    private void pyramid_lenient(int predefinedMax, int maxCoreAxisValue, int coreAxisSteps) {
        // Pyramid Arrival OneVMforAll - Lenient
        dataLoader.createGraphMain(340, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival OneVMforAllContainer - Lenient", "Pyramid_OneVMforAllContainer_Lenient_1409_%s", "Pyramid_OneVMforAll_Lenient_1409_%s", "Pyramid_OneVMforAll_Lenient.pdf", "1", "2", "3");

        // Pyramid Arrival OneVMPerTask - Lenient
        dataLoader.createGraphMain(130, 20, 260, "Pyramid Arrival OneVMPerTaskContainer - Lenient", "Pyramid_OneVMPerTaskContainer_Lenient_1409_%s", "Pyramid_OneVMPerTask_Lenient_1409_%s", "Pyramid_OneVMPerTask_Lenient.pdf", "1", "2", "3");

        // Pyramid Arrival StartParExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival StartParExceedContainer - Lenient", "Pyramid_StartParExceedContainer_Lenient_1409_%s", "Pyramid_StartParExceed_Lenient_1409_%s", "Pyramid_StartParExceed_Lenient.pdf", "1", "2", "3");

        // Pyramid Arrival StartParNotExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival StartParNotExceedContainer - Lenient", "Pyramid_StartParNotExceedContainer_Lenient_1409_%s", "Pyramid_StartParNotExceed_Lenient_1409_%s", "Pyramid_StartParNotExceed_Lenient.pdf", "1", "2", "3");

        // Pyramid Arrival AllParExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival AllParExceedContainer - Lenient", "Pyramid_AllParExceedContainer_Lenient_1409_%s", "Pyramid_AllParExceed_Lenient_1409_%s", "Pyramid_AllParExceed_Lenient.pdf", "1", "2", "3");

        // Pyramid Arrival AllParNotExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Pyramid Arrival AllParNotExceedContainer - Lenient", "Pyramid_AllParNotExceedContainer_Lenient_1409_%s", "Pyramid_AllParNotExceed_Lenient_1409_%s", "Pyramid_AllParNotExceed_Lenient.pdf", "1", "2", "3");
    }

    private void constant_strict(int predefinedMax, int maxCoreAxisValue, int coreAxisSteps) {
        // Constant Arrival OneVMforAll - Strict
        dataLoader.createGraphMain(240, coreAxisSteps, maxCoreAxisValue, "Constant Arrival OneVMforAllContainer - Strict", "Constant_OneVMforAllContainer_Strict_1409_%s", "Constant_OneVMforAll_Strict_1409_%s", "Constant_OneVMforAll_Strict.pdf", "1", "2", "3");

        // Constant Arrival OneVMPerTask - Strict
        dataLoader.createGraphMain(predefinedMax, 20, 260, "Constant Arrival OneVMPerTaskContainer - Strict", "Constant_OneVMPerTaskContainer_Strict_1409_%s", "Constant_OneVMPerTask_Strict_1409_%s", "Constant_OneVMPerTask_Strict.pdf", "1", "2", "3");

        // Constant Arrival StartParExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival StartParExceedContainer - Strict", "Constant_StartParExceedContainer_Strict_1409_%s", "Constant_StartParExceed_Strict_1409_%s", "Constant_StartParExceed_Strict.pdf", "1", "2", "3");

        // Constant Arrival StartParNotExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival StartParNotExceedContainer - Strict", "Constant_StartParNotExceedContainer_Strict_1409_%s", "Constant_StartParNotExceed_Strict_1409_%s", "Constant_StartParNotExceed_Strict.pdf", "1", "2", "3");

        // Constant Arrival AllParExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival AllParExceedContainer - Strict", "Constant_AllParExceedContainer_Strict_1409_%s", "Constant_AllParExceed_Strict_1409_%s", "Constant_AllParExceed_Strict.pdf", "1", "2", "3");

        // Constant Arrival AllParNotExceed - Strict
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival AllParNotExceedContainer - Strict", "Constant_AllParNotExceedContainer_Strict_1409_%s", "Constant_AllParNotExceed_Strict_1409_%s", "Constant_AllParNotExceed_Strict.pdf", "1", "2", "3");
    }

    private void constant_lenient(int predefinedMax, int maxCoreAxisValue, int coreAxisSteps) {
        // Constant Arrival OneVMforAll - Lenient
        dataLoader.createGraphMain(240, coreAxisSteps, maxCoreAxisValue, "Constant Arrival OneVMforAllContainer - Lenient", "Constant_OneVMforAllContainer_Lenient_1409_%s", "Constant_OneVMforAll_Lenient_1409_%s", "Constant_OneVMforAll_Lenient.pdf", "1", "2", "3");

        // Constant Arrival OneVMPerTask - Lenient
        dataLoader.createGraphMain(predefinedMax, 20, 260, "Constant Arrival OneVMPerTaskContainer - Lenient", "Constant_OneVMPerTaskContainer_Lenient_1409_%s", "Constant_OneVMPerTask_Lenient_1409_%s", "Constant_OneVMPerTask_Lenient.pdf", "1", "2", "3");

        // Constant Arrival StartParExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival StartParExceedContainer - Lenient", "Constant_StartParExceedContainer_Lenient_1409_%s", "Constant_StartParExceed_Lenient_1409_%s", "Constant_StartParExceed_Lenient.pdf", "1", "2", "3");

        // Constant Arrival StartParNotExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival StartParNotExceedContainer - Lenient", "Constant_StartParNotExceedContainer_Lenient_1409_%s", "Constant_StartParNotExceed_Lenient_1409_%s", "Constant_StartParNotExceed_Lenient.pdf", "1", "2", "3");

        // Constant Arrival AllParExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival AllParExceedContainer - Lenient", "Constant_AllParExceedContainer_Lenient_1409_%s", "Constant_AllParExceed_Lenient_1409_%s", "Constant_AllParExceed_Lenient.pdf", "1", "2", "3");

        // Constant Arrival AllParNotExceed - Lenient
        dataLoader.createGraphMain(predefinedMax, coreAxisSteps, maxCoreAxisValue, "Constant Arrival AllParNotExceedContainer - Lenient", "Constant_AllParNotExceedContainer_Lenient_1409_%s", "Constant_AllParNotExceed_Lenient_1409_%s", "Constant_AllParNotExceed_Lenient.pdf", "1", "2", "3");
    }

}

