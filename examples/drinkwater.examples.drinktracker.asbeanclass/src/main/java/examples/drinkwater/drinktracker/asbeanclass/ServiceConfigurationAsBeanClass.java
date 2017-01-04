package examples.drinkwater.drinktracker.asbeanclass;

import baseline.BaseLinefactory;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.ServiceScheme;
import examples.drinkwater.drinktracker.model.IWaterVolumeRepository;

public class ServiceConfigurationAsBeanClass extends ServiceConfigurationBuilder {

    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());

        getBuilder(IWaterVolumeRepository.class).withProperties("classpath:volume-repository.properties")
                .withInjectionStrategy(InjectionStrategy.Default);

        changeScheme(ServiceScheme.BeanClass);
    }


}
