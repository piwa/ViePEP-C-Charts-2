package at.ac.tuwien.infosys.viepepc.database.inmemory.services;

import at.ac.tuwien.infosys.viepepc.database.entities.container.ContainerConfiguration;
import at.ac.tuwien.infosys.viepepc.database.inmemory.database.InMemoryCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philippwaibel on 13/06/16. edited by Gerta Sheganaku
 */
@Component
public class CacheContainerService {

    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    public ContainerConfiguration getContainerConfigurationFromIdentifier(String identifier) throws Exception {
        for(ContainerConfiguration containerConfiguration : inMemoryCache.getContainerConfigurations()) {

            if(containerConfiguration.getName().equals(identifier)) {
                return containerConfiguration;
            }

        }
        return null;
    }

    public List<ContainerConfiguration> getAllPossibleContainerConfigurations(double requiredCpuLoad, double requiredRamLoad) {
        List<ContainerConfiguration> returnList = new ArrayList<>();

        for(ContainerConfiguration containerConfiguration : inMemoryCache.getContainerConfigurations()) {
            if (requiredCpuLoad <= containerConfiguration.getCPUPoints() && requiredRamLoad <= containerConfiguration.getRam()) {
                returnList.add(containerConfiguration);
            }
        }

        returnList.sort((config1, config2) -> (new Double(config1.getCores()).compareTo((new Double(config2.getCores())))));

        return returnList;
    }

}
