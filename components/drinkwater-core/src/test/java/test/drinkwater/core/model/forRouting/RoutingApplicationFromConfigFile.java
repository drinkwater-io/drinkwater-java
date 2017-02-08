package test.drinkwater.core.model.forRouting;

import drinkwater.ApplicationBuilder;
import drinkwater.IRoutingBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class RoutingApplicationFromConfigFile extends ApplicationBuilder {

    //we could take then from properties or db....
    private String[] services = new String[]{"A", "B", "C", "D"};
    private String[] subServices = new String[]{"X", "Y"};

    @Override
    public void configure() {

        //loop on each services
        for (String serviceName: services
             ) {
            addService("service" + serviceName, ITestRouting.class, TestRoutingImpl.class)
                    .asRest();

        }

        //loop on each subservices
        for (String subServiceName: subServices
                ) {
            addService("service" + subServiceName, ITestRouting.class, TestRoutingImpl.class)
                    .asRest();

        }

        addService("default_service", ITestRouting.class, TestRoutingImpl.class)
                .asRest();


        //add subrouting
        IRoutingBuilder subRouting = addService("subRouteurService", ITestRouting.class, TestRoutingImpl.class)
                .useTracing(true).asRouteur()
                .useHeader("SUBROUTINGHEADER");

        for (String subServiceName: subServices
                ) {
            subRouting.route(subServiceName, "service" + subServiceName);
        }

        //add first routing
        IRoutingBuilder routing = addService("frontService", ITestRouting.class, TestRoutingImpl.class)
                .useTracing(true).asRouteur()
                .useHeader("ROUTINGHEADER");

        for (String serviceName: services
                ) {
            routing.route(serviceName, "service" + serviceName);
        }

        //add subRouteur
        routing.route("sub", "subRouteurService");

        //add default service
        routing.route("default", "default_service");
        subRouting.route("default", "default_service");


    }
}
