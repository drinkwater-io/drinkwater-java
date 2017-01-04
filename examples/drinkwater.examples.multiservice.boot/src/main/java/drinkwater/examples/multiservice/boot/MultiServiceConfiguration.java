package drinkwater.examples.multiservice.boot;

import drinkwater.InjectionStrategy;
import drinkwater.ServiceConfiguration;
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

    public MultiServiceConfiguration() {
        ServiceConfiguration configD = ServiceConfiguration
                .forService(IServiceD.class)
                .useBeanClass(ServiceDImpl.class)
                .withProperties("classpath:multiservice.properties")
                .withInjectionStrategy(InjectionStrategy.Default)
                .name("serviceD")
                // .asRest()
                ;

        ServiceConfiguration configC = ServiceConfiguration
                .forService(IServiceC.class)
                .useBeanClass(ServiceCImpl.class)
                .withProperties("classpath:multiservice.properties")
                .withInjectionStrategy(InjectionStrategy.Default)
                .name("serviceC")
                //  .asRest()
                ;

        ServiceConfiguration configB = ServiceConfiguration
                .forService(IServiceB.class)
                .useBeanClass(ServiceBImpl.class)
                .name("serviceB")
                // .asRest()
                .dependsOn(configC, configD);

        ServiceConfiguration configA = ServiceConfiguration
                .forService(IServiceA.class)
                .useBeanClass(ServiceAImpl.class)
                .name("serviceA")
                .asRest()
                .dependsOn(configB);

        AddConfiguration(configD);
        AddConfiguration(configC);
        AddConfiguration(configB);
        AddConfiguration(configA);
    }


    @Override
    public void configure() {


    }
}
