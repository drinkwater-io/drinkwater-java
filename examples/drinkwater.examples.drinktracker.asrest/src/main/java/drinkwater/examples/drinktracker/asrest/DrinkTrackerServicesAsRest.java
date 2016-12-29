package drinkwater.examples.drinktracker.asrest;

import drinkwater.core.*;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServicesAsRest extends ServiceConfigurationBuilder{
    @Override
    public List<ServiceConfiguration> build(){

        ServiceConfiguration drinktrackerRepositoryService =ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:drinktracker.properties")
                .useBeanClass(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBeanClass(AccountService.class);

        ServiceConfiguration drinktrackerFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBeanClass(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration drinktrackerServiceAsRest =ServiceConfiguration
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
