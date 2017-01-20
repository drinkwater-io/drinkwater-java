package test.drinkwater.core.model.forRouting;

import drinkwater.IRoutingBuilder;
import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 5/01/2017.
 */
public class RoutingServiceConfigurationFromConfigFile extends ServiceConfigurationBuilder {

    //we could take then from properties or db....
    private String[] services = new String[]{"A", "B", "C", "D"};

    @Override
    public void configure() {

        //loop on each services
        for (String serviceName: services
             ) {
            addService("service" + serviceName, ITestRouting.class, TestRoutingImpl.class)
                    .withProperties("classpath:routing.properties")
                    .asRest();

        }

        IRoutingBuilder routing = addService("frontService", ITestRouting.class, TestRoutingImpl.class)
                .useTracing(true).asRouteur()
                .useHeader("ROUTINGHEADER");

        for (String serviceName: services
                ) {
            routing.route(serviceName, "service" + serviceName);
        }
    }
}
