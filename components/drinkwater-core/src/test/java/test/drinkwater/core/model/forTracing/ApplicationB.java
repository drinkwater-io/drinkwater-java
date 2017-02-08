package test.drinkwater.core.model.forTracing;

import drinkwater.ApplicationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ApplicationB extends ApplicationBuilder {

    private boolean useTracing;
    private boolean applicationTracing;

    public ApplicationB(boolean applicationTracing, boolean useTracing) {
        this.useTracing = useTracing;
        this.applicationTracing = applicationTracing;
    }
    @Override
    public void configure() {
        useTracing(applicationTracing);

        addService("serviceC", IServiceC.class).addInitialProperty("drinkwater.rest.port", 9999)
                .useTracing(useTracing)
                .asRemote();
        addService("serviceB", IServiceB.class, new ServiceBImpl(), "serviceC")
                .useTracing(useTracing)
                .addInitialProperty("drinkwater.rest.port", 8888).asRest();
    }
}
