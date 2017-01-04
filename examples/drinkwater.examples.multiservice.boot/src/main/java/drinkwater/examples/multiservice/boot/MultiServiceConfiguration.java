package drinkwater.examples.multiservice.boot;

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
public class MultiServiceConfiguration extends ServiceConfigurationBuilder {

    //TODO change this to use the configure method instead and use names in the dependson method
    public MultiServiceConfiguration() {

//        ServiceConfiguration configD = new ServiceConfiguration()
//                .forService(IServiceD.class)
//                .useBeanClass(ServiceDImpl.class)
//                .withProperties("classpath:multiservice.properties")
//                .withInjectionStrategy(InjectionStrategy.Default)
//                .name("serviceD")
//                // .asRest()
//                ;
//
//        ServiceConfiguration configC = new ServiceConfiguration()
//                .forService(IServiceC.class)
//                .useBeanClass(ServiceCImpl.class)
//                .withProperties("classpath:multiservice.properties")
//                .withInjectionStrategy(InjectionStrategy.Default)
//                .name("serviceC")
//                //  .asRest()
//                ;
//
//        ServiceConfiguration configB = new ServiceConfiguration()
//                .forService(IServiceB.class)
//                .useBeanClass(ServiceBImpl.class)
//                .name("serviceB")
//                // .asRest()
//                .dependsOn(configC.getServiceName(), configD.getServiceName());
//
//        ServiceConfiguration configA = new ServiceConfiguration()
//                .forService(IServiceA.class)
//                .useBeanClass(ServiceAImpl.class)
//                .name("serviceA")
//                .asRest()
//                .dependsOn(configB.getServiceName());
//
//        addConfiguration(configD);
//        addConfiguration(configC);
//        addConfiguration(configB);
//        addConfiguration(configA);
    }

    @Override
    public void configure() {
        addService("serviceD", IServiceD.class, ServiceDImpl.class, "classpath:multiservice.properties", InjectionStrategy.Default);
        addService("serviceC", IServiceC.class, ServiceCImpl.class, "classpath:multiservice.properties", InjectionStrategy.Default);
        addService("serviceB", IServiceB.class, ServiceBImpl.class, "serviceC", "serviceD");
        addService("serviceA", IServiceA.class, ServiceAImpl.class, "serviceB").asRest();
    }
}
