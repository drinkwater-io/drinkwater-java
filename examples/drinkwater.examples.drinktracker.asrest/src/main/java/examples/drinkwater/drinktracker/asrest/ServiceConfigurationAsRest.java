package examples.drinkwater.drinktracker.asrest;

import baseline.BaseLinefactory;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.ServiceScheme;
import examples.drinkwater.drinktracker.model.IWaterVolumeRepository;

public class ServiceConfigurationAsRest extends ServiceConfigurationBuilder {


    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());

        getBuilder(IWaterVolumeRepository.class).withProperties("classpath:drinktracker.properties")
                .withInjectionStrategy(InjectionStrategy.Default);

        changeScheme(ServiceScheme.Rest);
    }


}
