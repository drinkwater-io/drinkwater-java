package drinkwater;


import javaslang.collection.List;

import java.util.ArrayList;

/**
 * Created by A406775 on 29/12/2016.
 */
public class ServiceConfigurationBuilder {

    private java.util.List<IServiceConfiguration> configurations = new ArrayList<>();

    public void AddConfiguration(IServiceConfiguration configuration) {
        configurations.add(configuration);
    }

    public java.util.List<IServiceConfiguration> getConfigurations() {
        return configurations;
    }

    public IServiceConfiguration getConfiguration(String serviceName) {
        return List.ofAll(getConfigurations())
                .filter(conf -> conf.getServiceName().equals(serviceName))
                .get();
    }

    public void useMock(String serviceName, Object beanToUse) {

        ServiceConfiguration config = (ServiceConfiguration) getConfiguration(serviceName);

        config.setTargetBean(beanToUse);
        config.setSheme(ServiceScheme.Mock);


    }

    public void configure() {

    }
}
