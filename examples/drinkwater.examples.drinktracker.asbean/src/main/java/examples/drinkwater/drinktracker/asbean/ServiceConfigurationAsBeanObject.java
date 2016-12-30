package examples.drinkwater.drinktracker.asbean;

import baseline.BaseLinefactory;
import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;

import java.util.List;

public class ServiceConfigurationAsBeanObject extends ServiceConfigurationBuilder {

    @Override
    public List<IServiceConfiguration> build() {

        List<ServiceConfiguration> baseLine = BaseLinefactory.createServices();

        return javaslang.collection.List.ofAll(baseLine)
                .map(s -> (IServiceConfiguration) s) // cast
                .toJavaList();

    }
}
