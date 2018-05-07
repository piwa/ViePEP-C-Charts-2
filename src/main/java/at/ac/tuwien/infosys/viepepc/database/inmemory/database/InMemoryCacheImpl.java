package at.ac.tuwien.infosys.viepepc.database.inmemory.database;


import at.ac.tuwien.infosys.viepepc.database.entities.container.ContainerConfiguration;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Getter
public class InMemoryCacheImpl {
    private List<ContainerConfiguration> containerConfigurations = new ArrayList<>();

    public void clear() {
        containerConfigurations = new ArrayList<>();
    }

    public void addContainerConfiguration(ContainerConfiguration containerConfiguration) {
        containerConfigurations.add(containerConfiguration);
    }

    public void addAllContainerConfiguration(List<ContainerConfiguration> configurations) {
        for(ContainerConfiguration configuration : configurations) {
            addContainerConfiguration(configuration);
        }
    }

}
