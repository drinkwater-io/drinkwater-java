package examples.drinkwater.drinktracker.asbean;

import baseline.BaseLinefactory;
import drinkwater.ServiceConfigurationBuilder;

public class ServiceConfigurationAsBeanObject extends ServiceConfigurationBuilder {

    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());
    }
}
