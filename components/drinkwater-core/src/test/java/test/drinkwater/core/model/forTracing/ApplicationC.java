package test.drinkwater.core.model.forTracing;

import drinkwater.ApplicationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ApplicationC extends ApplicationBuilder {

    private boolean useTracing;

    public ApplicationC(boolean useTracing) {
        this.useTracing = useTracing;
    }

    @Override
    public void configure() {
        addService("serviceC", IServiceC.class, new ServiceCImpl())
                .addInitialProperty("drinkwater.rest.port", 9999)
                .useTracing(useTracing)
                .asRest();
    }
}
