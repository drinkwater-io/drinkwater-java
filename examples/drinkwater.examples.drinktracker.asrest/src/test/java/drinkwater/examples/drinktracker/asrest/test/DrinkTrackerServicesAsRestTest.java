package drinkwater.examples.drinktracker.asrest.test;

import drinkwater.boot.DrinkWaterBoot;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.drinktracker.model.Account;
import drinkwater.examples.drinktracker.model.IAccountService;
import drinkwater.test.HttpUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class DrinkTrackerServicesAsRestTest extends HttpUnitTest
{
    static DrinkWaterApplication app;
    static DrinkWaterBoot booter;
    static String apiEnpoint = "http://localhost:8889";

    @BeforeClass
    public static void setup() throws Exception {
        booter = new DrinkWaterBoot();
        booter.start();
        app = booter.getDrinkWaterMain().getDrinkWaterApplication();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        booter.stop();
    }

    @Test
    public void shouldSaveNumberThroughRest() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account account = accountService.createAccount("cedric", "secret");

        httpPost(apiEnpoint + "/idrinktrackerservice/rest/volume?volume=10",
                "{'accountName':'cedric','authenticated':true, 'accountPassword':'secret'}")
                .expectsBody("\"00010\"");
    }
}
