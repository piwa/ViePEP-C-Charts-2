package at.ac.tuwien.infosys.viepepc.database.services;

import at.ac.tuwien.infosys.viepepc.database.entities.ContainerActionsDTO;
import at.ac.tuwien.infosys.viepepc.database.entities.WorkflowDTO;
import at.ac.tuwien.infosys.viepepc.database.entities.container.ContainerConfiguration;
import at.ac.tuwien.infosys.viepepc.database.inmemory.services.CacheContainerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.jfree.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by philippwaibel on 24/04/2017.
 */
@Component
@Slf4j
public class ContainerActionsService {

    @Autowired
    private CacheContainerService cacheContainerService;

    @Value("${spring.datasource.url}")
    private String databaseUrl = "jdbc:mysql://localhost:3306/";
    @Value("${spring.datasource.url.parameter}")
    private String databaseUrlParameter = "?autoReconnect=true&useSSL=false";
    @Value("${spring.datasource.username}")
    private String databaseUsername = "viepep";
    @Value("${spring.datasource.password}")
    private String databasePassword = "";

    @Value("${container.cpu.cost}")
    private double containerCpuCost;
    @Value("${container.ram.cost}")
    private double containerRamCost;


    /**
     * @param dbName
     * @param firstDate
     * @param maxDurationInSeconds
     * @param isBaseline           if this is true, than replace the 0_ variable name with 3_, since in baseline only quadcores were allowed
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public List<ContainerActionsDTO> getContainerActionsDTOs(String dbName, Date firstDate, int maxDurationInSeconds, boolean isBaseline, int min, int max) throws SQLException, ParseException {

        Connection conn = DriverManager.getConnection(databaseUrl.concat(dbName).concat(databaseUrlParameter), databaseUsername, databasePassword);

        Statement stmt = conn.createStatement();
        String sql;


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResultSet rs;
        //step 1 : load them all
        sql = "select * from container_reporting_action order by timestamp asc;";
        rs = stmt.executeQuery(sql);

        List<ContainerActionsDTO> tmpContainerActionsList = new ArrayList<>();

        int localMax = 0;
        while (rs.next()) {
            ContainerActionsDTO dto = new ContainerActionsDTO();
            Date timestamp = simpleDateFormat.parse(rs.getString("timestamp"));
            String vmID = rs.getString("containerid");
            String vmAction = rs.getString("docker_action");
            String vmTypeId = rs.getString("container_configuration_name");

            dto.setContainerID(vmID);
            dto.setContainerAction(vmAction);
            dto.setDate(timestamp);
            dto.setContainerConfigurationName(vmTypeId);
            dto.setDate(new Date(timestamp.getTime() - firstDate.getTime()));

            if(tmpContainerActionsList.stream().noneMatch(cActionsDTO -> cActionsDTO.getContainerID().equals(dto.getContainerID()) && cActionsDTO.getContainerAction().equals(dto.getContainerAction())))
            {
                tmpContainerActionsList.add(dto);
            }

        }

        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        Date deadlineTime = new Date(tmpContainerActionsList.get(0).getDate().getTime() + randomNum * 60 * 1000);

        Map<String, ContainerActionsDTO> startEvents = new HashMap<>();
        Map<String, ContainerActionsDTO> stopEvents = new HashMap<>();
        for (ContainerActionsDTO action : tmpContainerActionsList) {
            if(action != null && !org.springframework.util.StringUtils.isEmpty(action.getContainerAction())) {
                if (action.getContainerAction().equalsIgnoreCase("START")) {
                    startEvents.put(action.getContainerID(), action);
                } else if (action.getContainerAction().equalsIgnoreCase("STOPPED") || action.getContainerAction().equalsIgnoreCase("FAILED")) {
                    stopEvents.put(action.getContainerID(), action);
                }
            }
        }

        List<String> unusedStopEvents = new ArrayList<>();
        stopEvents.forEach((s, containerActionsDTO) -> {
            if (containerActionsDTO.getDate().after(deadlineTime)) {
                tmpContainerActionsList.remove(containerActionsDTO);
                unusedStopEvents.add(s);
            }
        });
        unusedStopEvents.forEach(s -> stopEvents.remove(s));

        startEvents.forEach((containerId, containerActionsDTO) -> {
            if(!stopEvents.containsKey(containerId) || containerId.equalsIgnoreCase("Container_8_Service8_b636f06c-f841-4f1a-a551-23edcd1cdd20") ||
                    containerId.equalsIgnoreCase("Container_2_Service1_e41ea00b-8cc3-4db8-ac34-6809debc3084") ||
                    containerId.equalsIgnoreCase("Container_2_Service4_fc549657-9132-41b3-a530-5224be7aaaef") ||
                    containerActionsDTO.getDate().after(deadlineTime)) {
                tmpContainerActionsList.remove(containerActionsDTO);
                unusedContainer.add(containerId);
            }
        });


        //step3: fill in missing entries:
        GregorianCalendar start = new GregorianCalendar();
        long time = firstDate.getTime();
        start.setTime(new Date(0L));

        //sort per second
        Map<Integer, List<ContainerActionsDTO>> perSecondMap = new HashMap<>();

        for (ContainerActionsDTO containerActionsDTO : tmpContainerActionsList) {
            Calendar current = new GregorianCalendar();
            current.setTime(containerActionsDTO.getDate());
//            current.set(Calendar.SECOND, 0);
            containerActionsDTO.setDate(current.getTime());
            int seconds= (int) TimeUnit.MILLISECONDS.toSeconds(containerActionsDTO.getDate().getTime());
            List<ContainerActionsDTO> containerActionsDTOs = perSecondMap.get(seconds);
            if (containerActionsDTOs == null) {
                containerActionsDTOs = new ArrayList<>();
            }
            if (containerActionsDTO.getContainerAction().equalsIgnoreCase("START")) {
                localMax = Math.max(seconds, localMax);
            }
            containerActionsDTOs.add(containerActionsDTO);
            perSecondMap.put(seconds, containerActionsDTOs);
        }


        ContainerActionsDTO lastAction = new ContainerActionsDTO();
        lastAction.setDate(start.getTime());
        lastAction.setContainerAction("");
        lastAction.setCoreAmount(0);
        lastAction.setContainerID("");

        //if last action (localMax) was less than 5 minutes before from the maxDurationInMinutes, set the maxDurationInMinutes to lastAction+5

        if ((localMax + 150) < maxDurationInSeconds) {
            maxDurationInSeconds = localMax + 150;
        }


//        if ((localMax) > maxDurationInSeconds) {
//            //ignore
//        } else if ((localMax + 5) > maxDurationInSeconds) {
//            maxDurationInSeconds = localMax + (int) (Math.ceil((maxDurationInSeconds - localMax) / 5.0) * 5);
//        } else {
//            maxDurationInSeconds = localMax + (int) (Math.ceil((maxDurationInSeconds - localMax) / 5.0) * 5);
//        }


        for (int i = 0; i <= maxDurationInSeconds; i++) {
            List<ContainerActionsDTO> containerActionsDTOs = perSecondMap.get(i);
            if (containerActionsDTOs == null) {
                containerActionsDTOs = new ArrayList<>();
            }
            if (containerActionsDTOs.size() == 0) {
                if (i == 0) {
                    ContainerActionsDTO clone = copy(lastAction);
                    containerActionsDTOs.add(clone);
                    perSecondMap.put(i, containerActionsDTOs);
                    continue;
                }
                lastAction = copy(lastAction);
                GregorianCalendar current = new GregorianCalendar();
                current.setTime(lastAction.getDate());
                int seconds = current.get(Calendar.SECOND);
                current.set(Calendar.SECOND, seconds + 1);
                lastAction.setDate(current.getTime());
                if (i >= maxDurationInSeconds) {
                    lastAction.setCoreAmount(0);
                }

                containerActionsDTOs.add(lastAction);
            } else {
                ContainerActionsDTO newAction = new ContainerActionsDTO();
                ContainerActionsDTO first = containerActionsDTOs.get(0);
                newAction.setDate(first.getDate());
                newAction.setContainerID(lastAction.getContainerID());
                double sum = 0;
                for (ContainerActionsDTO containerActionsDTO : containerActionsDTOs) {
                    String containerid = newAction.getContainerID() + containerActionsDTO.getContainerID() + "_" + containerActionsDTO.getContainerAction() + ",";
                    newAction.setContainerID(containerid);
                    newAction.setContainerAction(containerActionsDTO.getContainerAction());
                    sum += getCoreCount(containerActionsDTO);
                }
                sum = sum + lastAction.getCoreAmount();
                if (i >= maxDurationInSeconds) {//set last value to 0
                    newAction.setCoreAmount(0);
                } else {
                    newAction.setCoreAmount(sum);
                }
                containerActionsDTOs = new ArrayList<>();
                containerActionsDTOs.add(newAction);
                lastAction = copy(newAction);
            }

            perSecondMap.put(i, containerActionsDTOs);
        }

        List<ContainerActionsDTO> res = new ArrayList<>();
        for (Integer integer : perSecondMap.keySet()) {
            if (integer > maxDurationInSeconds) {
                continue; // no need to add them
            }
            res.addAll(perSecondMap.get(integer));
        }
        return res;
    }


    public ArrayList<String> unusedContainer = new ArrayList<>();

    private ContainerActionsDTO copy(ContainerActionsDTO lastAction) {

        ContainerActionsDTO copy = new ContainerActionsDTO();
        copy.setDate(lastAction.getDate());
        copy.setContainerID(lastAction.getContainerID());
        copy.setCoreAmount(lastAction.getCoreAmount());
        copy.setContainerAction(lastAction.getContainerAction());
        copy.setContainerConfigurationName(lastAction.getContainerConfigurationName());
        return copy;
    }



    /**
     * @param isBaseline if this is true, than replace the 0_ variable name with 3_, since in baseline only quadcores were allowed
     * @return [0] internal costs; [1] external costs
     * @throws SQLException
     * @throws ParseException
     */
    public double[] getCoreUsage(String dbName, WorkflowDTO firstArrivedWorkflow, WorkflowDTO lastArrivedWorkflow, boolean isBaseline) throws SQLException, ParseException {
        Double internalCosts = 0.0;

        Connection conn = DriverManager.getConnection(databaseUrl.concat(dbName).concat(databaseUrlParameter), databaseUsername, databasePassword);

        Statement stmt = conn.createStatement();
        String sql;
        sql = "select * from container_reporting_action;";
        ResultSet rs = stmt.executeQuery(sql);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //STEP 5: Extract data from result set

        List<ContainerActionsDTO> results = new ArrayList<>();


        while (rs.next()) {
            //Retrieve by column name
            ContainerActionsDTO dto = new ContainerActionsDTO();
            Date timestamp = simpleDateFormat.parse(rs.getString("timestamp"));
            String containerID = rs.getString("containerid");
            String containerAction = rs.getString("docker_action");
            String containerTypeId = rs.getString("container_configuration_name");
            if (isBaseline) {
                //    vmID = vmID.replace("0_", "3_");
            }
            dto.setContainerID(containerID);
            dto.setContainerAction(containerAction);
            dto.setDate(timestamp);
            dto.setContainerConfigurationName(containerTypeId);
            //dto.setDate(new Date(timestamp.getTime() - firstArrivedWorkflow.getArrivedAt().getTime()));
            results.add(dto);
        }

        Collections.sort(results, Comparator.comparing(ContainerActionsDTO::getDate));

        List<ContainerActionsDTO> stopResults = new ArrayList<>();
        for (ContainerActionsDTO result : results) {
            if (result.getContainerAction().equalsIgnoreCase("STOPPED") || result.getContainerAction().equalsIgnoreCase("FAILED")) {
                stopResults.add(result);
            }
        }
//        stopResults.addAll(results);

        double totalSeconds = 0;

        for (ContainerActionsDTO action : results) {

            if (action.getContainerAction().equalsIgnoreCase("START") && !unusedContainer.contains(action.getContainerID())) {
                long milliseconds = 0;
                for (ContainerActionsDTO action2 : stopResults) {
                    if (action2.getContainerID().equals(action.getContainerID())) {
                        milliseconds = action2.getDate().getTime() - action.getDate().getTime();
                        stopResults.remove(action2);
                        break;
                    }
                }
                if (milliseconds == 0) {
                    milliseconds = lastArrivedWorkflow.getFinishedAt().getTime() - action.getDate().getTime();
                }
                double inSeconds = milliseconds / 1000;
                totalSeconds = totalSeconds + inSeconds/60;

                try {
                    ContainerConfiguration containerConfiguration = cacheContainerService.getContainerConfigurationFromIdentifier(action.getContainerConfigurationName());
                    internalCosts = internalCosts + (containerConfiguration.getCores() * containerCpuCost * inSeconds  + containerConfiguration.getRam() / 1000 * containerRamCost * inSeconds);

//                    String coreString = action.getContainerConfigurationName().substring(0, action.getContainerConfigurationName().indexOf("_"));
//                    double cores = Double.valueOf(coreString) / 100;
//                    internalCosts = internalCosts + (cores * containerCpuCost * inSeconds  + 10 / 1000 * containerRamCost * inSeconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        log.info(dbName + " core costs: " + internalCosts + ", leasing duration: " + totalSeconds);
        rs.close();
        stmt.close();

        return new double[]{internalCosts, 0, totalSeconds};
    }

    public double getCoreCount(ContainerActionsDTO containerAction) {

        double result = 0.25;

        try {
            ContainerConfiguration containerConfiguration = cacheContainerService.getContainerConfigurationFromIdentifier(containerAction.getContainerConfigurationName());
            result = containerConfiguration.getCores();

//            String cores = containerAction.getContainerConfigurationName().substring(0, containerAction.getContainerConfigurationName().indexOf("_"));
//            result = Double.valueOf(cores)/ 100;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(containerAction.getContainerAction().equalsIgnoreCase("START")) {
            return result;
        }
        else {
            return result * -1;
        }

    }

}
