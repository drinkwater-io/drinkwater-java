package drinkwater.examples.drinktracker.asbeanclass;

import drinkwater.core.*;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServiceAsBeanClass extends ServiceConfigurationBuilder {
    @Override
    public List<ServiceConfiguration> build() {

        ServiceConfiguration volumeRepositoryService = ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:volume-repository.properties")
                .useBeanClass(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService = ServiceConfiguration
                .forService(IAccountService.class)
                .useBeanClass(AccountService.class);

        ServiceConfiguration volumeFormatter = ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBeanClass(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration volumeService = ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBeanClass(DrinkTrackerService.class)
                .dependsOn(accountService, volumeFormatter, volumeRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return javaslang.collection.List.of(
                accountService,
                volumeFormatter,
                volumeRepositoryService,
                volumeService
        ).toJavaList();
    }
}
