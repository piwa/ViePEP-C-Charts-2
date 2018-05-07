package at.ac.tuwien.infosys.viepepc.database.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTransferElementDTO {
    private String name;
    private String parent;
    private String type;
    private int vm;

}
