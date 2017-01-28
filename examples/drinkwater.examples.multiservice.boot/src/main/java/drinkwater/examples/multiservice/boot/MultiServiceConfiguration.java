package drinkwater.examples.multiservice.boot;

import drinkwater.ServiceConfigurationBuilder;
import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceB;
import drinkwater.examples.multiservice.IServiceC;
import drinkwater.examples.multiservice.IServiceD;
import drinkwater.examples.multiservice.impl.ServiceAImpl;
import drinkwater.examples.multiservice.impl.ServiceBImpl;
import drinkwater.examples.multiservice.impl.ServiceCImpl;
import drinkwater.examples.multiservice.impl.ServiceDImpl;

/**
 * Created by A406775 on 2/01/2017.
 */
public class MultiServiceConfiguration extends ServiceConfigurationBuilder {

    @Override
    public void configure() {
        addService("serviceD", IServiceD.class, ServiceDImpl.class);
        addService("serviceC", IServiceC.class, ServiceCImpl.class);
        addService("serviceB", IServiceB.class, ServiceBImpl.class, "serviceC", "serviceD");
        addService("serviceA", IServiceA.class, ServiceAImpl.class, "serviceB").asRest();
    }
}
