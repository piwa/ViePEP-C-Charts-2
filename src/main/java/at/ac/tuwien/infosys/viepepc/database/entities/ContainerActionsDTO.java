package at.ac.tuwien.infosys.viepepc.database.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContainerActionsDTO {

    Date date;
    double coreAmount;
    private String ContainerAction;
    private String ContainerID;
    private String ContainerConfigurationName;


}

