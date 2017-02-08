package test.drinkwater.core.model.forTracing;

import drinkwater.ApplicationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ApplicationC extends ApplicationBuilder {

    private boolean applicationTracing;
    private boolean useTracing;
    private Class eventLoggerClass;

    public ApplicationC(boolean applicationTracing, boolean useTracing, Class eventLoggerClass) {
        this.useTracing = useTracing;
        this.eventLoggerClass = eventLoggerClass;
        this.applicationTracing = applicationTracing;
    }

    @Override
    public void configure() {
        useTracing(applicationTracing);
        useEventLogger(eventLoggerClass);
        addService("serviceC", IServiceC.class, new ServiceCImpl())
                .addInitialProperty("drinkwater.rest.port", 9999)
                .useTracing(useTracing)
                .asRest();
    }
}
