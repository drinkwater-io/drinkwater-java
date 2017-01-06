package test.drinkwater.core.model.forTracing;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceCConfiguration extends ServiceConfigurationBuilder {

    private boolean useTracing;

    public ServiceCConfiguration(boolean useTracing) {
        this.useTracing = useTracing;
    }

    @Override
    public void configure() {
        addService("serviceC", IServiceC.class, new ServiceCImpl())
                .withProperty("drinkwater.rest.port", 9999)
                .useTracing(useTracing)
                .asRest();
    }
}
