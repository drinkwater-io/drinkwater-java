package test.drinkwater.examples.drinktracker.asbeanclass;


import drinkwater.core.DrinkWaterApplication;
import examples.drinkwater.drinktracker.asbeanclass.ApplicationAsBeanClass;
import examples.drinkwater.drinktracker.model.Account;
import examples.drinkwater.drinktracker.model.IAccountService;
import examples.drinkwater.drinktracker.model.IWaterVolumeFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class ServiceConfigurationAsBeanClassTest {
    static DrinkWaterApplication app;

    @BeforeClass
    public static void setup() throws Exception {
        app = DrinkWaterApplication.create();
        app.addServiceBuilder(new ApplicationAsBeanClass());
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

        accountService.clearAccounts();

    }

    public void shouldLogin() throws Exception {
        IAccountService accountService = app.getService(IAccountService.class);
        Account account = accountService.createAccount("cedric", "secret");
        account = accountService.login("cedric", "secret");
        assertTrue(account.isAuthenticated());

        accountService.clearAccounts();
    }
}
