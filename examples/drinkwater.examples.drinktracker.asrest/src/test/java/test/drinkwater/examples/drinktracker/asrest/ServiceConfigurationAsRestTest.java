package test.drinkwater.examples.drinktracker.asrest;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import examples.drinkwater.drinktracker.asrest.ServiceConfigurationAsRest;
import examples.drinkwater.drinktracker.model.Account;
import examples.drinkwater.drinktracker.model.IAccountService;
import examples.drinkwater.drinktracker.model.IDrinkTrackerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class ServiceConfigurationAsRestTest extends HttpUnitTest {
    private static DrinkWaterApplication app;
    private static String apiEnpoint = "http://127.0.0.1:8889/WaterVolumeFileRepository";

    @BeforeClass
    public static void setup() throws Exception {
        app = DrinkWaterApplication.create();
        app.addServiceBuilder(new ServiceConfigurationAsRest());
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }

    @Test
    public void shouldSaveNumberThroughRest() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account acc = accountService.createAccount("cedric", "secret");

        httpPostRequestString(apiEnpoint + "/WaterVolume?volume=10",
                "{'accountName':'cedric','authenticated':true, 'accountPassword':'secret'}");


        accountService.clearAccounts();
    }

    @Test
    public void shouldSaveNumberThroughRestWithServiceReference() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        IDrinkTrackerService drinkTracker = app.getService(IDrinkTrackerService.class);
        accountService.clearAccounts();
        Account account = accountService.createAccount("cedric", "secret");

        account = accountService.login("cedric", "secret");
        String savedVolume = drinkTracker.saveVolume(account, 10);

        assertEquals("00010", savedVolume);

        accountService.clearAccounts();
    }

    @Test
    public void shouldTestGetWithObject() throws Exception {
        Account account = Account.from("1", "cedric", "secret", true);

        IDrinkTrackerService drinkTracker = app.getService(IDrinkTrackerService.class);

        List<String> volumes = drinkTracker.getVolumes(account);

        assertEquals(0, volumes.size());
    }

    @Test
    public void shouldClearVolumes() throws Exception {
        Account account = Account.from("1", "cedric", "secret", true);

        IDrinkTrackerService drinkTracker = app.getService(IDrinkTrackerService.class);

        drinkTracker.clearVolumes(account);

        assertTrue(true);

    }

}
