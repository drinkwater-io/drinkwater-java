package drinkwater.examples.drinktracker.asbeanclass;

import drinkwater.core.*;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServiceAsBeanClass extends ServiceConfigurationBuilder {
    @Override
    public List<IServiceConfiguration> build() {

        IServiceConfiguration volumeRepositoryService = ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:volume-repository.properties")
                .useBeanClass(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        IServiceConfiguration accountService = ServiceConfiguration
                .forService(IAccountService.class)
                .useBeanClass(AccountService.class);

        IServiceConfiguration volumeFormatter = ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBeanClass(DefaultWaterVolumeFormatter.class);

        IServiceConfiguration volumeService = ServiceConfiguration
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