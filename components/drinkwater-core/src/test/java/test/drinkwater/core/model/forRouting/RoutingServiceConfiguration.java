package test.drinkwater.core.model.forRouting;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class RoutingServiceConfiguration extends ServiceConfigurationBuilder {


    @Override
    public void configure() {
        addService("serviceA", ITestRouting.class, TestRoutingImpl.class).asRest()
                .addInitialProperty("serviceA.someServiceSpecificProperty", "propertyFromA");
        addService("serviceB", ITestRouting.class, TestRoutingImpl.class).asRest()
                .addInitialProperty("serviceB.someServiceSpecificProperty", "propertyFromB");
        addService("serviceC", ITestRouting.class, TestRoutingImpl.class)
                .asRest().addInitialProperty("serviceC.someServiceSpecificProperty", "propertyFromC");

        addService("frontService", ITestRouting.class, TestRoutingImpl.class)
                .useTracing(true).asRouteur()
                .useHeader("ROUTINGHEADER")
                .route("A", "serviceA")
                .route("B", "serviceB")
                .route("C", "serviceC");

    }
}
