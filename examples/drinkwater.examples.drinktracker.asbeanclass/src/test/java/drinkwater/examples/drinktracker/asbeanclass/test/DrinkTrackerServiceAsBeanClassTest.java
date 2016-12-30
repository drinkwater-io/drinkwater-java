package drinkwater.examples.drinktracker.asbeanclass.test;


import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.drinktracker.asbeanclass.DrinkTrackerServiceAsBeanClass;
import drinkwater.examples.drinktracker.model.Account;
import drinkwater.examples.drinktracker.model.IAccountService;
import drinkwater.examples.drinktracker.model.IWaterVolumeFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class DrinkTrackerServiceAsBeanClassTest {
    static DrinkWaterApplication app;

    @BeforeClass
    public static void setup() throws Exception {
        app = new DrinkWaterApplication();
        app.addServiceBuilder(new DrinkTrackerServiceAsBeanClass());
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }

    @Test
    public void shouldFormatVolume() {
        IWaterVolumeFormatter formatter = app.getService(IWaterVolumeFormatter.class);
        String result = formatter.formatVolume("10");
        assertEquals("010", result);

    }

    @Test
    public void shouldCreateAccount() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account account = accountService.createAccount("cedric", "secret");
        assertEquals(account.getAccountName(), "cedric");
        assertEquals(account.getAccountPassword(), "secret");
        assertNotNull(account.getAcountId());
        assertFalse(account.isAuthenticated());

        accountService.clear();

    }

    public void shouldLogin() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account account = accountService.createAccount("cedric", "secret");
        account = accountService.login("cedric", "secret");
        assertTrue(account.isAuthenticated());

        accountService.clear();
    }
}
