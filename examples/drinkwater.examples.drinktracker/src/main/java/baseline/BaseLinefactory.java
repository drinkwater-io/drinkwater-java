package baseline;

import drinkwater.InjectionStrategy;
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

        ServiceConfiguration iaccountService = new ServiceConfiguration()
                .forService(IAccountService.class)
                .withInjectionStrategy(InjectionStrategy.Default)
                .useBean(accountService);

        ServiceConfiguration ivolumeRepositoryService = new ServiceConfiguration()
                .forService(IWaterVolumeRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default)
                .useBean(waterVolumeFileRepository);

        ServiceConfiguration ivolumeFormatter = new ServiceConfiguration()
                .forService(IWaterVolumeFormatter.class)
                .withInjectionStrategy(InjectionStrategy.Default)
                .useBean(waterVolumeFormatter);

        ServiceConfiguration ivolumeService = new ServiceConfiguration()
                .forService(IDrinkTrackerService.class)
                .withInjectionStrategy(InjectionStrategy.Default)
                .useBean(drinkTrackerService)
                .dependsOn(iaccountService.getServiceName(), ivolumeFormatter.getServiceName(), ivolumeRepositoryService.getServiceName());

        //FIXME order is important here, we should sort by deps...
        return javaslang.collection.List.of(
                iaccountService,
                ivolumeRepositoryService,
                ivolumeFormatter,
                ivolumeService
        ).toJavaList();
    }
}
