package test.drinkwater.core.model.forTracing;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceBConfiguration extends ServiceConfigurationBuilder {

    private boolean useTracing;

    public ServiceBConfiguration(boolean useTracing) {
        this.useTracing = useTracing;
    }
    @Override
    public void configure() {

        addService("serviceC", IServiceC.class).addInitialProperty("drinkwater.rest.port", 9999)
                .useTracing(useTracing)
                .asRemote();
        addService("serviceB", IServiceB.class, new ServiceBImpl(), "serviceC")
                .useTracing(useTracing)
                .addInitialProperty("drinkwater.rest.port", 8888).asRest();
    }
}
