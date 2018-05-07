package at.ac.tuwien.infosys.viepepc.database.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Getter
@Setter
public class WorkflowDTO {
    private String name;
    private Date arrivedAt;
    private Date deadline;
    private Date finishedAt;


}
