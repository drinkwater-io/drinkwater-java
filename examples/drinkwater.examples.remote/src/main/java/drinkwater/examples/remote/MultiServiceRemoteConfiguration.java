package drinkwater.examples.remote;

import drinkwater.InjectionStrategy;
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
public class MultiServiceRemoteConfiguration extends ServiceConfigurationBuilder {

    @Override
    public void configure() {
        addService("serviceD", IServiceD.class, ServiceDImpl.class, "classpath:multiservice.properties", InjectionStrategy.Default);
        addService("serviceC", IServiceC.class, ServiceCImpl.class, "classpath:multiservice.properties", InjectionStrategy.Default).asRest();
        addService("serviceB", IServiceB.class, ServiceBImpl.class, "serviceC", "serviceD");
        addService("serviceA", IServiceA.class, ServiceAImpl.class, "serviceB").asRest();
    }
}
