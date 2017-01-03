package drinkwater.examples.multiservice.boot;

import drinkwater.IServiceConfiguration;
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

import java.util.List;

/**
 * Created by A406775 on 2/01/2017.
 */
public class MultiServiceConfiguration extends ServiceConfigurationBuilder {
    @Override
    public List<IServiceConfiguration> build() {

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

        return javaslang.collection.List.of(configD, configC, configB, configA)
                .map(s -> (IServiceConfiguration) s) // cast
                .toJavaList();

    }
}
