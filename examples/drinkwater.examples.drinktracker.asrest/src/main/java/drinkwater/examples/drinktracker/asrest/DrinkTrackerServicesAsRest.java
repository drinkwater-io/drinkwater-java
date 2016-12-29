package drinkwater.examples.drinktracker.asrest;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.drinktracker.model.*;

@DrinkWaterApplicationConfig
public class DrinkTrackerServicesAsRest {
    public ServiceConfigurationCollection getServiceConfigurations(){

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
        return ServiceConfigurationCollection.of(
                accountService,
                drinktrackerFormatter,
                drinktrackerRepositoryService,
                drinktrackerServiceAsRest);
    }
}
