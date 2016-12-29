package drinkwater.examples.drinktracker.asbean;

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
                .useBean(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(AccountService.class);

        ServiceConfiguration volumeFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration volumeService =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBean(DrinkTrackerService.class)
                .dependsOn(accountService, volumeFormatter, volumeRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return ServiceConfigurationCollection.of(
                accountService,
                volumeFormatter,
                volumeRepositoryService,
                volumeService);
    }
}
