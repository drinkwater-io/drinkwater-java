package examples.drinkwater.drinktracker.asbeanclass;

import baseline.BaseLinefactory;
import drinkwater.ApplicationBuilder;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceScheme;
import examples.drinkwater.drinktracker.model.IWaterVolumeRepository;

public class ApplicationAsBeanClass extends ApplicationBuilder {

    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());

        getBuilder(IWaterVolumeRepository.class).withProperties("classpath:volume-repository.properties")
                .withInjectionStrategy(InjectionStrategy.Default);

        changeScheme(ServiceScheme.BeanClass);
    }


}
