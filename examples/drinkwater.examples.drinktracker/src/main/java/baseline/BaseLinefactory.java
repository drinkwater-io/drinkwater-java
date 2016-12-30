package baseline;

import drinkwater.ServiceConfiguration;
import examples.drinkwater.drinktracker.model.*;

import java.util.List;

/**
 * Created by A406775 on 30/12/2016.
 */
public class BaseLinefactory {

    // create baseline that uses simple bean (created with new keyword)
    public static List<ServiceConfiguration> createServices() {
        AccountService accountService = new AccountService();
        DefaultWaterVolumeFormatter waterVolumeFormatter = new DefaultWaterVolumeFormatter();
        WaterVolumeFileRepository waterVolumeFileRepository = new WaterVolumeFileRepository("c:/temp");
        DrinkTrackerService drinkTrackerService = new DrinkTrackerService();

        ServiceConfiguration iaccountService = ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(accountService);

        ServiceConfiguration ivolumeRepositoryService = ServiceConfiguration
                .forService(IWaterVolumeRepository.class)
                .useBean(waterVolumeFileRepository);

        ServiceConfiguration ivolumeFormatter = ServiceConfiguration
                .forService(IWaterVolumeFormatter.class)
                .useBean(waterVolumeFormatter);

        ServiceConfiguration ivolumeService = ServiceConfiguration
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
