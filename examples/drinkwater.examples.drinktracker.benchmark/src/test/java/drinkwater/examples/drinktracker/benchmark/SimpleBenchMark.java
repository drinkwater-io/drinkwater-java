package drinkwater.examples.drinktracker.benchmark;

import drinkwater.ServiceConfigurationBuilder;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.drinktracker.asbeanclass.DrinkTrackerServiceAsBeanClass;
import drinkwater.examples.drinktracker.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by A406775 on 28/12/2016.
 */
public class SimpleBenchMark {

    private int LOOP_COUNT = 1;

    @Test
    public void performBenchMark() throws Exception {

        long timeWithoutDW = benchMarkWithoutDW();
        long timeAsBeanClass = benchMarkThisConfig(new DrinkTrackerServiceAsBeanClass());
        long timeAsRest = 0;
        //long timeAsRest = benchMarkThisConfig(new DrinkTrackerServicesAsRest());

        printReport(timeWithoutDW, timeAsBeanClass, timeAsRest);

        //just to be green
        Assert.assertTrue(true);
    }

    private long benchMarkWithoutDW() throws Exception {
        //create service using classes
        AccountService accountService = new AccountService();
        DrinkTrackerService numberService = new DrinkTrackerService();
        numberService.setWaterVolumeFormatter(new DefaultWaterVolumeFormatter());
        numberService.setWaterVolumeRepository(new WaterVolumeFileRepository("c:/temp"));
        numberService.setAccountService(accountService);

        return dowork(accountService, numberService);
    }

    private long benchMarkThisConfig(ServiceConfigurationBuilder builder) throws Exception {

        DrinkWaterApplication app = new DrinkWaterApplication();
        try {

            app.addServiceBuilder(builder);
            app.start();

            //get ref to the services
            IAccountService accountDWService = app.getService(IAccountService.class);
            IDrinkTrackerService numberDWService = app.getService(IDrinkTrackerService.class);

            //start bench
            return dowork(accountDWService, numberDWService);
        } finally {
            app.stop();
        }

    }

    private void printReport(long timeWithoutDW, long timeAsBeanClass, long timeAsRest) {
        long diff_with_bean_class = timeAsBeanClass - timeWithoutDW;
        long diff_with_rest = timeAsRest - timeWithoutDW;

        System.out.println("BENCHMARK WITH loop count : " + LOOP_COUNT);
        System.out.println("---------------------------------------------------");
        System.out.println("");
        System.out.println("test with DIRECT_OBJECT took            :  " + TimeUnit.NANOSECONDS.toMillis(timeWithoutDW) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeWithoutDW) + " sec)");
        System.out.println("test with BEANCLASS took                :  " + TimeUnit.NANOSECONDS.toMillis(timeAsBeanClass) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeAsBeanClass) + " sec)");
        System.out.println("test with REST took                     :  " + TimeUnit.NANOSECONDS.toMillis(timeAsRest) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeAsRest) + " sec)");
        System.out.println("");
        System.out.println("test with BEANCLASS  was slower  by     :  " + TimeUnit.NANOSECONDS.toMillis(diff_with_bean_class) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(diff_with_bean_class) + " sec)");
        System.out.println("test with RESTCLASS was slower   by     :  " + TimeUnit.NANOSECONDS.toMillis(diff_with_rest) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(diff_with_rest) + " sec)");
        System.out.println("");
        System.out.println("---------------------------------------------------");
    }

    private long dowork(IAccountService accountService, IDrinkTrackerService numberService) throws Exception {
        Account acc = accountService.createAccount("cedric", "secret");

        //start counter here
        long startTime = System.nanoTime();
        for (int i = 0; i < LOOP_COUNT; i++) {
            acc = accountService.login("cedric", "secret");
            numberService.saveVolume(acc, 10);
            numberService.saveVolume(acc, 20);
            numberService.saveVolume(acc, 30);
            numberService.saveVolume(acc, 40);
            numberService.saveVolume(acc, 50);
            numberService.saveVolume(acc, 60);
            List<String> numbers = numberService.getVolumes(acc);

            //perfrom some assertions
            Assert.assertEquals(6, numbers.size());
            //delete file and logoff
            numberService.clearVolumes(acc);
            accountService.logoff(acc);
        }

        accountService.clear();

        long ellapsedTime = System.nanoTime() - startTime;

        return ellapsedTime;
    }
}
