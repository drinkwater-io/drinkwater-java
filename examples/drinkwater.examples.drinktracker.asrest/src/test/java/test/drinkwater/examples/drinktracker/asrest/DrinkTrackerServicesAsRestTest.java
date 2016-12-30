package test.drinkwater.examples.drinktracker.asrest;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import examples.drinkwater.drinktracker.asrest.DrinkTrackerServicesAsRest;
import examples.drinkwater.drinktracker.model.Account;
import examples.drinkwater.drinktracker.model.IAccountService;
import examples.drinkwater.drinktracker.model.IDrinkTrackerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class DrinkTrackerServicesAsRestTest extends HttpUnitTest {
    static DrinkWaterApplication app;
    static String apiEnpoint = "http://localhost:8889/examples.drinkwater.drinktracker.model.idrinktrackerservice";

    @BeforeClass
    public static void setup() throws Exception {
        app = new DrinkWaterApplication();
        app.addServiceBuilder(new DrinkTrackerServicesAsRest());
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }

    @Test
    public void shouldSaveNumberThroughRest() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account account = accountService.createAccount("cedric", "secret");

        httpPost(apiEnpoint + "/volume?volume=10",
                "{'accountName':'cedric','authenticated':true, 'accountPassword':'secret'}")
                .expectsBody("\"00010\"");

        accountService.clear();
    }

    //
    @Test
    public void shouldSaveNumberThroughRestWithServiceReference() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        IDrinkTrackerService drinkTracker = app.getService(IDrinkTrackerService.class);
        Account account = accountService.createAccount("cedric", "secret");

        account = accountService.login("cedric", "secret");
        String savedVolume = drinkTracker.saveVolume(account, 10);

        assertEquals("00010", savedVolume);

        accountService.clear();
    }


}