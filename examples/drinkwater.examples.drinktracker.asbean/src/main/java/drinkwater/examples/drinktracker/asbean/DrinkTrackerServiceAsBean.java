package drinkwater.examples.drinktracker.asbean;

import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.examples.drinktracker.model.*;

import java.util.List;

public class DrinkTrackerServiceAsBean extends ServiceConfigurationBuilder {

    @Override
    public List<IServiceConfiguration> build() {

        //create service using classes
        AccountService accountService = new AccountService();
        DefaultWaterVolumeFormatter waterVolumeFormatter = new DefaultWaterVolumeFormatter();
        WaterVolumeFileRepository waterVolumeFileRepository = new WaterVolumeFileRepository("c:/temp");
        DrinkTrackerService drinkTrackerService = new DrinkTrackerService();

        IServiceConfiguration iaccountService = ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(accountService);

        IServiceConfiguration ivolumeRepositoryService = ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .useBean(waterVolumeFileRepository);

        IServiceConfiguration ivolumeFormatter = ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(waterVolumeFormatter);

        IServiceConfiguration ivolumeService = ServiceConfiguration
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
