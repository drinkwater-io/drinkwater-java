package drinkwater.examples.drinktracker.asbean;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.drinktracker.model.*;

@DrinkWaterApplicationConfig
public class DWNumberApplicationConfig {
    public ServiceConfigurationCollection getServiceConfigurations(){

        ServiceConfiguration numberRepositoryService =ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:first-number-repository.properties")
                .useBean(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(AccountService.class);

        ServiceConfiguration numberFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration numberService =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBean(DrinkTrackerService.class)
                .dependsOn(accountService, numberFormatter, numberRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return ServiceConfigurationCollection.of(
                accountService,
                numberFormatter,
                numberRepositoryService,
                numberService);
    }
}
