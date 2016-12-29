package drinkwater.examples.drinktracker.asbean;

import drinkwater.core.*;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServiceAsBean extends ServiceConfigurationBuilder{

    @Override
    public List<ServiceConfiguration> build(){

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
        return javaslang.collection.List.of(
                iaccountService,
                ivolumeRepositoryService,
                ivolumeFormatter,
                ivolumeService
        ).toJavaList();
    }
}
