package test.drinkwater.examples.drinktracker.benchmark;

import drinkwater.ServiceConfigurationBuilder;
import drinkwater.core.DrinkWaterApplication;
import examples.drinkwater.drinktracker.asbean.ServiceConfigurationAsBeanObject;
import examples.drinkwater.drinktracker.asbeanclass.ServiceConfigurationAsBeanClass;
import examples.drinkwater.drinktracker.asrest.ServiceConfigurationAsRest;
import examples.drinkwater.drinktracker.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by A406775 on 28/12/2016.
 */
public class SimpleBenchMark {

    private int LOOP_COUNT = 1;

    @Test
    public void performBenchMark() throws Exception {

        long timeAsRest = 0;
        long timeAsBeanObject = 0;
        long timeAsBeanClass = 0;
        long timeWithoutDW = 0;

//        //without DrinkWater
        timeWithoutDW = benchMarkWithoutDW();
//        //rest
        timeAsRest = benchMarkThisConfig(new ServiceConfigurationAsRest());
//        //beanobject
        timeAsBeanObject = benchMarkThisConfig(new ServiceConfigurationAsBeanObject());
//        //as beanClass
        timeAsBeanClass = benchMarkThisConfig(new ServiceConfigurationAsBeanClass());

        printReport(timeWithoutDW, timeAsBeanObject, timeAsBeanClass, timeAsRest);

        //just to be green
        assertTrue(true);
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

        DrinkWaterApplication app = DrinkWaterApplication.create();
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

    private void printReport(long timeWithoutDW, long timeAsBeanObject, long timeAsBeanClass, long timeAsRest) {
        long diff_with_bean_object = timeAsBeanObject - timeWithoutDW;
        long diff_with_bean_class = timeAsBeanClass - timeWithoutDW;
        long diff_with_rest = timeAsRest - timeWithoutDW;

        System.out.println("BENCHMARK WITH loop count : " + LOOP_COUNT);
        System.out.println("---------------------------------------------------");
        System.out.println("");
        System.out.println("test with DIRECT_OBJECT took            :  " + TimeUnit.NANOSECONDS.toMillis(timeWithoutDW) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeWithoutDW) + " sec)");
        System.out.println("test with BEANOBJECT took               :  " + TimeUnit.NANOSECONDS.toMillis(timeAsBeanObject) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeAsBeanObject) + " sec)");
        System.out.println("test with BEANCLASS took                :  " + TimeUnit.NANOSECONDS.toMillis(timeAsBeanClass) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeAsBeanClass) + " sec)");
        System.out.println("test with REST took                     :  " + TimeUnit.NANOSECONDS.toMillis(timeAsRest) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(timeAsRest) + " sec)");
        System.out.println("");
        System.out.println("test with BEANOBJECT  was slower  by    :  " + TimeUnit.NANOSECONDS.toMillis(diff_with_bean_object) + " millis (" + TimeUnit.NANOSECONDS.toSeconds(diff_with_bean_object) + " sec)");
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

            assertTrue(true);
        }

        accountService.clearAccounts();

        long ellapsedTime = System.nanoTime() - startTime;

        return ellapsedTime;
    }
}
