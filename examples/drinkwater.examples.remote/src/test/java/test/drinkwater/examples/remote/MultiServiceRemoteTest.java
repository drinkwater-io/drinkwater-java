package test.drinkwater.examples.remote;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.remote.MultiServiceRemoteConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class MultiServiceRemoteTest {

    static DrinkWaterApplication app;

    @BeforeClass
    public static void setup() throws Exception {
        app = DrinkWaterApplication.create();
        MultiServiceRemoteConfiguration config = new MultiServiceRemoteConfiguration();
        app.addServiceBuilder(config);
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }


    @Test
    public void ShouldCallRemoteService() {


    }


}
