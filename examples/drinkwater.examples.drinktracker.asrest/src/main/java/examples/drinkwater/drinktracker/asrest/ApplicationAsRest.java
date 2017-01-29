package examples.drinkwater.drinktracker.asrest;

import baseline.BaseLinefactory;
import drinkwater.ApplicationBuilder;
import drinkwater.ServiceScheme;

public class ApplicationAsRest extends ApplicationBuilder {


    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());

//        getBuilder(IWaterVolumeRepository.class)
//                //.withProperties("classpath:drinktracker.properties")
//                .withInjectionStrategy(InjectionStrategy.Default);

        changeScheme(ServiceScheme.Rest);
    }


}
