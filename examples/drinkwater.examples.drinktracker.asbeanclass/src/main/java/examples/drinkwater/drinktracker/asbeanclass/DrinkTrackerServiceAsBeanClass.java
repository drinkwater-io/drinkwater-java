package examples.drinkwater.drinktracker.asbeanclass;

import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import examples.drinkwater.drinktracker.model.*;

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
