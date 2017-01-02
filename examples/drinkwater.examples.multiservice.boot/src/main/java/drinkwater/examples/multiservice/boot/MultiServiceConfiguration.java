package drinkwater.examples.multiservice.boot;

import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceB;
import drinkwater.examples.multiservice.IServiceC;
import drinkwater.examples.multiservice.impl.ServiceAImpl;
import drinkwater.examples.multiservice.impl.ServiceBImpl;
import drinkwater.examples.multiservice.impl.ServiceCImpl;

import java.util.List;

/**
 * Created by A406775 on 2/01/2017.
 */
public class MultiServiceConfiguration extends ServiceConfigurationBuilder {
    @Override
    public List<IServiceConfiguration> build() {

        ServiceConfiguration configC = ServiceConfiguration
                .forService(IServiceC.class)
                .useBeanClass(ServiceCImpl.class)
                .asRest();

        ServiceConfiguration configB = ServiceConfiguration
                .forService(IServiceB.class)
                .useBeanClass(ServiceBImpl.class)
                .asRest()
                .dependsOn(configC);

        ServiceConfiguration configA = ServiceConfiguration
                .forService(IServiceA.class)
                .useBeanClass(ServiceAImpl.class)
                .asRest()
                .dependsOn(configB);

        return javaslang.collection.List.of(configC, configB, configA)
                .map(s -> (IServiceConfiguration) s) // cast
                .toJavaList();

    }
}
