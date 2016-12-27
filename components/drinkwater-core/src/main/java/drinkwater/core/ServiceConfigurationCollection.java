package drinkwater.core;

import javaslang.collection.List;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfigurationCollection implements Iterable<ServiceConfiguration> {
    private List<ServiceConfiguration> configurations;

    public ServiceConfigurationCollection(List<ServiceConfiguration> configurations) {
        this.configurations = List.ofAll(configurations);
    }

    public List<ServiceConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ServiceConfiguration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public Iterator<ServiceConfiguration> iterator() {
        return  configurations.iterator();
    }

    @Override
    public void forEach(Consumer<? super ServiceConfiguration> action) {
        configurations.forEach(action);
    }

    @Override
    public Spliterator<ServiceConfiguration> spliterator() {
        return this.configurations.spliterator();
    }

    public static ServiceConfigurationCollection of(ServiceConfiguration... configs){
        return new ServiceConfigurationCollection(List.of(configs));
    }
}
