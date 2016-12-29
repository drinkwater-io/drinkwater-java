package drinkwater.examples.drinktracker.asrest;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.drinktracker.model.*;

@DrinkWaterApplicationConfig
public class DWNumberApplicationAsRest {
    public ServiceConfigurationCollection getServiceConfigurations(){

        ServiceConfiguration numberRepositoryService =ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .withProperties("classpath:numberRepositoryService.properties")
                .useBean(WaterVolumeFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(AccountService.class);

        ServiceConfiguration numberFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(DefaultWaterVolumeFormatter.class);

        ServiceConfiguration numberServiceAsRest =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBean(DrinkTrackerService.class)
                .asRest()
                .dependsOn(accountService, numberFormatter, numberRepositoryService);

        //FIXME order is important here, it should not be
        return ServiceConfigurationCollection.of(
                accountService,
                numberFormatter,
                numberRepositoryService,
                numberServiceAsRest);
    }
}
