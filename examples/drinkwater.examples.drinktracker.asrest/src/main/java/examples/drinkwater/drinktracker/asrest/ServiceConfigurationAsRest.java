package examples.drinkwater.drinktracker.asrest;

import baseline.BaseLinefactory;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.ServiceScheme;

public class ServiceConfigurationAsRest extends ServiceConfigurationBuilder {


    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());

//        getBuilder(IWaterVolumeRepository.class)
//                //.withProperties("classpath:drinktracker.properties")
//                .withInjectionStrategy(InjectionStrategy.Default);

        changeScheme(ServiceScheme.Rest);
    }


}
