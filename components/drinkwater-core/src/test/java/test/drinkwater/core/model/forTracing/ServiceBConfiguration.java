package test.drinkwater.core.model.forTracing;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceBConfiguration extends ServiceConfigurationBuilder {

    @Override
    public void configure() {

        addService("serviceC", IServiceC.class).withProperty("drinkwater.rest.port", 9999)
                .useTracing(true)
                .asRemote();
        addService("serviceB", IServiceB.class, new ServiceBImpl(), "serviceC")
                .useTracing(true)
                .withProperty("drinkwater.rest.port", 8888).asRest();
    }
}
