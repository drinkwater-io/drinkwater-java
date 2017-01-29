package test.drinkwater.core.model.forTracing;

import drinkwater.ApplicationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ApplicationB extends ApplicationBuilder {

    private boolean useTracing;

    public ApplicationB(boolean useTracing) {
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
