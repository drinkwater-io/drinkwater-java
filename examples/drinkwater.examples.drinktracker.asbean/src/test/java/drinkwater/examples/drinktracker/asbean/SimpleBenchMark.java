package drinkwater.examples.drinktracker.asbean;

import drinkwater.boot.DrinkWaterBoot;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.drinktracker.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by A406775 on 28/12/2016.
 */
public class SimpleBenchMark {

    private int LOOP_COUNT = 1;
    DrinkWaterApplication app;
    DrinkWaterBoot booter;

    @Before
    public void setup() throws Exception {
        booter = new DrinkWaterBoot();
        booter.start();
        app = booter.getDrinkWaterMain().getDrinkWaterApplication();
    }

    @After
    public void tearDown() throws Exception {
        booter.stop();
    }

    @Test
    public void performBenchMark() throws Exception {

        //get service from DrinkWater configuration was set in APP
        IAccountService accountDWService = app.getService(IAccountService.class);
        IDrinkTrackerService numberDWService = app.getService(IDrinkTrackerService.class);

        //create service using classes
        AccountService accountService = new AccountService();
        DrinkTrackerService numberService = new DrinkTrackerService();
        numberService.setWaterVolumeFormatter(new DefaultWaterVolumeFormatter());
        numberService.setWaterVolumeRepository(new WaterVolumeFileRepository("c:/temp"));
        numberService.setAccountService(accountService);

        long startObjectTime = System.nanoTime();
        dowork(accountService, numberService);
        long estimatedObjectTime = System.nanoTime() - startObjectTime;

        long startDWTime = System.nanoTime();
        dowork(accountDWService, numberDWService);
        long estimatedDWTime = System.nanoTime() - startDWTime;



        long difference = estimatedDWTime - estimatedObjectTime;

        System.out.println("BENCHMARK WITH loop count : " + LOOP_COUNT);
        System.out.println("---------------------------------------------------");
        System.out.println("");
        System.out.println("test with DRINKWATER took               :" + TimeUnit.NANOSECONDS.toMillis(estimatedDWTime) + " millis ("+TimeUnit.NANOSECONDS.toSeconds(estimatedDWTime) + " sec)");
        System.out.println("test with DIRECT_OBJECT took            :" + TimeUnit.NANOSECONDS.toMillis(estimatedObjectTime) + " millis ("+TimeUnit.NANOSECONDS.toSeconds(estimatedObjectTime) + " sec)");
        System.out.println("test with DIRECT_OBJECT was faster  by  :" + TimeUnit.NANOSECONDS.toMillis(difference) + " millis ("+TimeUnit.NANOSECONDS.toSeconds(difference) + " sec)");
        System.out.println("");

        assertTrue(true);
    }

    private void dowork(IAccountService accountService, IDrinkTrackerService numberService) throws Exception {
        Account acc = accountService.createAccount("cedric", "secret");
        for (int i = 0; i < LOOP_COUNT ; i++) {
            acc = accountService.login("cedric", "secret");
            numberService.saveVolume(acc, 10);
            numberService.saveVolume(acc, 20);
            numberService.saveVolume(acc, 30);
            numberService.saveVolume(acc, 40);
            numberService.saveVolume(acc, 50);
            numberService.saveVolume(acc, 60);
            List<String> numbers = numberService.getVolumes(acc);

            //perfrom some assertions
            assertEquals(6, numbers.size());
            //delete file and logoff
            numberService.clearVolumes(acc);
            accountService.logoff(acc);
        }

        accountService.clear();
    }
}
