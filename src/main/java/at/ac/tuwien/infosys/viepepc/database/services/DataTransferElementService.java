package at.ac.tuwien.infosys.viepepc.database.services;

import at.ac.tuwien.infosys.viepepc.database.entities.DataTransferElementDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by philippwaibel on 24/04/2017.
 */
@Component
public class DataTransferElementService {

    @Value("${spring.datasource.url}")
    private String databaseUrl = "jdbc:mysql://localhost:3306/";
    @Value("${spring.datasource.url.parameter}")
    private String databaseUrlParameter = "?autoReconnect=true&useSSL=false";
    @Value("${spring.datasource.username}")
    private String databaseUsername = "viepep";
    @Value("${spring.datasource.password}")
    private String databasePassword = "";


    public Integer getDataTransferCosts(String dbName) throws SQLException {
        Connection conn = DriverManager.getConnection(databaseUrl.concat(dbName).concat(databaseUrlParameter), databaseUsername, databasePassword);

        Integer dataTransferCosts = 0;

        Statement stmt = conn.createStatement();
        String sql;
        sql = " select *  from virtual_machine;\n";
        ResultSet rs = stmt.executeQuery(sql);

        Map<Integer, String> vms = new HashMap<>();
        vms.put(100, "internal");
        vms.put(0, "internal");

        while (rs.next()) {
            //Retrieve by column name
            if (rs.getString("vm_type").startsWith("AWS")) {
                vms.put(rs.getInt("id"), "external");
            } else {
                vms.put(rs.getInt("id"), "internal");
            }
        }

        rs.close();

        Statement stmt1 = conn.createStatement();
        String sql1;

        sql1 = " select e.name, e.parent_id, p.schedule_at_vm_id, p.service_type_id  from element as e, process_step_element as p, workflow_element as w where w.id<>e.id and e.id = p.id;\n";
        ResultSet rs1 = stmt1.executeQuery(sql1);

        Map<String, DataTransferElementDTO> elements = new HashMap<>();

        while (rs1.next()) {
            //Retrieve by column name

            DataTransferElementDTO dto = new DataTransferElementDTO();
            dto.setName(rs1.getString("name"));
            dto.setParent(rs1.getString("parent_id"));
            dto.setType(rs1.getString("service_type_id"));
            dto.setVm(rs1.getInt("schedule_at_vm_id"));

            elements.put(rs1.getString("name"), dto);
        }

        for (DataTransferElementDTO value : elements.values()) {
            if (value.getParent() != null) {
                DataTransferElementDTO parent = elements.get(value.getParent());
                if (parent == null) {
                    continue;
                }
                if (parent.getVm() != 0) {
                    parent.setVm(value.getVm());
                    elements.put(parent.getName(), parent);
                }
            }
        }


        for (DataTransferElementDTO value : elements.values()) {
            DataTransferElementDTO parent = elements.get(value.getParent());
            if (parent == null) {
                continue;
            }
            if (!vms.get(value.getVm()).equals(vms.get(parent.getVm()))) {
                if (value.getType().equals("Service1")) dataTransferCosts += 2;
                else if (value.getType().equals("Service2")) dataTransferCosts += 4;
                else if (value.getType().equals("Service3")) dataTransferCosts += 6;
                else if (value.getType().equals("Service4")) dataTransferCosts += 2;
                else if (value.getType().equals("Service5")) dataTransferCosts += 4;
                else if (value.getType().equals("Service6")) dataTransferCosts += 6;
                else if (value.getType().equals("Service7")) dataTransferCosts += 2;
                else if (value.getType().equals("Service8")) dataTransferCosts += 4;
                else if (value.getType().equals("Service9")) dataTransferCosts += 6;
                else if (value.getType().equals("Service10")) dataTransferCosts += 2;
                else if (value.getType().equals("Service11")) dataTransferCosts += 2;

            }

        }

        System.out.println("DataTransferCosts:" + dataTransferCosts);

        rs1.close();
        return dataTransferCosts;
    }

}
