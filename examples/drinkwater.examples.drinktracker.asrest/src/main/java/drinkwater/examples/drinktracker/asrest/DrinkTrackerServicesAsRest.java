package drinkwater.examples.drinktracker.asrest;

import drinkwater.core.*;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServicesAsRest extends ServiceConfigurationBuilder{
    @Override
    public List<IServiceConfiguration> build(){

        IServiceConfiguration drinktrackerRepositoryService =ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:drinktracker.properties")
                .useBeanClass(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        IServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBeanClass(AccountService.class);

        IServiceConfiguration drinktrackerFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBeanClass(DefaultWaterVolumeFormatter.class);

        IServiceConfiguration drinktrackerServiceAsRest =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBeanClass(DrinkTrackerService.class)
                .asRest()
                .dependsOn(accountService, drinktrackerFormatter, drinktrackerRepositoryService);

        //FIXME order is important here, it should not be
        return javaslang.collection.List.of(
                accountService,
                drinktrackerFormatter,
                drinktrackerRepositoryService,
                drinktrackerServiceAsRest
        ).toJavaList();
    }
}
