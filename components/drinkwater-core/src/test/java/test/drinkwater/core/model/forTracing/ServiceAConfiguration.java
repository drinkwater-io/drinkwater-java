package test.drinkwater.core.model.forTracing;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceAConfiguration extends ServiceConfigurationBuilder {

    private boolean useTracing;
    private boolean useTracingOnlyOnEntry;

    public ServiceAConfiguration(boolean useTracing, boolean useTracingOnlyOnEntry) {
        this.useTracing = useTracing;
        this.useTracingOnlyOnEntry = useTracingOnlyOnEntry;
    }

    @Override
    public void configure() {
        boolean traced = useTracing;
        boolean traceb = useTracing;
        boolean tracea = useTracing;

        if (useTracingOnlyOnEntry) {
            traced = false;
            traceb = false;
            tracea = true;
        }
        addService("serviceD", IServiceD.class, ServiceDImpl.class)
                .useTracing(traced);
        addService("serviceB", IServiceB.class).withProperty("drinkwater.rest.port", 8888)
                .useTracing(traceb).asRemote();
        addService("serviceA", IServiceA.class, new ServiceAImpl(), "serviceD", "serviceB")
                .useTracing(tracea).withProperty("drinkwater.rest.port", 7777).asRest();
    }
}