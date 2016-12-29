package drinkwater.examples.drinktracker.asbean;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.drinktracker.model.*;

@DrinkWaterApplicationConfig
public class DrinkTrackerServiceAsBean {

    public ServiceConfigurationCollection getServiceConfigurations(){

        //create service using classes
        AccountService accountService = new AccountService();
        DefaultWaterVolumeFormatter waterVolumeFormatter = new DefaultWaterVolumeFormatter();
        WaterVolumeFileRepository waterVolumeFileRepository = new WaterVolumeFileRepository("c:/temp");
        DrinkTrackerService drinkTrackerService = new DrinkTrackerService();

        ServiceConfiguration iaccountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(accountService);

        ServiceConfiguration ivolumeRepositoryService = ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .useBean(waterVolumeFileRepository);

        ServiceConfiguration ivolumeFormatter =ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(waterVolumeFormatter);

        ServiceConfiguration ivolumeService =ServiceConfiguration
                .forService(IDrinkTrackerService.class)
                .useBean(drinkTrackerService)
                .dependsOn(iaccountService, ivolumeFormatter, ivolumeRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return ServiceConfigurationCollection.of(
                iaccountService,
                ivolumeRepositoryService,
                ivolumeFormatter,
                ivolumeService);
    }
}
