package at.ac.tuwien.infosys.viepepc.database.services;

import at.ac.tuwien.infosys.viepepc.database.entities.WorkflowDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by philippwaibel on 24/04/2017.
 */
@Component
public class WorkflowService {

    @Value("${spring.datasource.url}")
    private String databaseUrl = "jdbc:mysql://localhost:3306/";
    @Value("${spring.datasource.url.parameter}")
    private String databaseUrlParameter = "?autoReconnect=true&useSSL=false";
    @Value("${spring.datasource.username}")
    private String databaseUsername = "viepep";
    @Value("${spring.datasource.password}")
    private String databasePassword = "";

    @Value("${deadline.timezone.adaptation.hours}")
    private int deadlineTimeZoneAdaptation;

    public List<WorkflowDTO> getWorkflowDTOs(String dbName) throws SQLException, ParseException {
        Connection conn = DriverManager.getConnection(databaseUrl.concat(dbName).concat(databaseUrlParameter), databaseUsername, databasePassword);
        //STEP 4: Execute a query
        Statement stmt = conn.createStatement();
        String sql;
        sql = " select distinct e.name, w.arrived_at, FROM_UNIXTIME(e.deadline/1000) as deadline, e.finished_at from element as e, workflow_element as w where e.type='workflow' and e.id=w.id and e.finished_at is not null and e.finished_at<>0 order by w.arrived_at;\n";
        ResultSet rs = stmt.executeQuery(sql);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //STEP 5: Extract data from result set

        List<WorkflowDTO> results = new ArrayList<>();

        while (rs.next()) {
            //Retrieve by column name

            String name = rs.getString("e.name");
            java.util.Date arrivedAt = simpleDateFormat.parse(rs.getString("w.arrived_at"));
            java.util.Date deadline = simpleDateFormat.parse(rs.getString("deadline"));
            String finishedAt1 = rs.getString("e.finished_at");
            java.util.Date finishedAt = simpleDateFormat.parse(finishedAt1);

            deadline.setTime(deadline.getTime() - deadlineTimeZoneAdaptation * 60 * 60 * 1000);

            WorkflowDTO dto = new WorkflowDTO();
            dto.setName(name);
            dto.setArrivedAt(arrivedAt);
            dto.setDeadline(deadline);
            dto.setFinishedAt(finishedAt);
            results.add(dto);
        }
        rs.close();
        stmt.close();
        Collections.sort(results, Comparator.comparing(WorkflowDTO::getArrivedAt));
        conn.close();
        return results;
    }

    public List<WorkflowDTO> getWorkflowDTOsArrivals(String dbName) throws SQLException, ParseException {

        Connection conn = DriverManager.getConnection(databaseUrl.concat(dbName).concat(databaseUrlParameter), databaseUsername, databasePassword);
        //STEP 4: Execute a query
        Statement stmt = conn.createStatement();
        String sql;

        sql = " select * from element as e, workflow_element as w where e.id=w.id and e.type='workflow';\n";
        ResultSet rs = stmt.executeQuery(sql);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //STEP 5: Extract data from result set

        List<WorkflowDTO> results = new ArrayList<>();

        while (rs.next()) {
            //Retrieve by column name

            String name = rs.getString("name");
            java.util.Date arrivedAt = simpleDateFormat.parse(rs.getString("arrived_at"));
            //  Date deadline = simpleDateFormat.parse(rs.getString("deadline"));
            //  String finishedAt1 = rs.getString("e.finished_at");
            //  Date finishedAt = simpleDateFormat.parse(finishedAt1);


            WorkflowDTO dto = new WorkflowDTO();
            dto.setName(name);
            dto.setArrivedAt(arrivedAt);
            //  dto.setDeadline(deadline);
            // dto.setFinishedAt(finishedAt);
            results.add(dto);
        }
        rs.close();
        stmt.close();
//        Collections.sort(results, new Comparator<WorkflowDTO>() {
//            @Override
//            public int compare(WorkflowDTO o1, WorkflowDTO o2) {
//                if (o1.getArrivedAt().before(o2.getArrivedAt())) {
//                    return -1;
//                } else if (o2.getArrivedAt().before(o1.getArrivedAt())) {
//                    return 1;
//                }
//                return 0;
//            }
//        });
        conn.close();
        return results;
    }


    public WorkflowDTO getLastExecutedWorkflow(List<WorkflowDTO> workflowDTOList) {
        WorkflowDTO lastExecutedWorkflow = null;
        for (WorkflowDTO result : workflowDTOList) {
            if (lastExecutedWorkflow == null) {
                lastExecutedWorkflow = result;
            }
            if (lastExecutedWorkflow.getFinishedAt().before(result.getFinishedAt())) {
                lastExecutedWorkflow = result;
            }
        }
        return lastExecutedWorkflow;
    }


}
