package drinkwater.examples.drinktracker.asbeanclass;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.drinktracker.model.*;

@DrinkWaterApplicationConfig
public class DrinkTrackerServiceAsBeanClass {
    public ServiceConfigurationCollection getServiceConfigurations(){

        ServiceConfiguration volumeRepositoryService =ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:volume-repository.properties")
                .useBeanClass(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBeanClass(AccountService.class);

        ServiceConfiguration volumeFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBeanClass(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration volumeService =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBeanClass(DrinkTrackerService.class)
                .dependsOn(accountService, volumeFormatter, volumeRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return ServiceConfigurationCollection.of(
                accountService,
                volumeFormatter,
                volumeRepositoryService,
                volumeService);
    }
}
