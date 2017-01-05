package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.drinkwater.core.model.forTracing.ServiceAConfiguration;
import test.drinkwater.core.model.forTracing.ServiceBConfiguration;

/**
 * Created by A406775 on 5/01/2017.
 */
public class TracingTest extends HttpUnitTest {

    static DrinkWaterApplication app_A;
    static DrinkWaterApplication app_B;

    @BeforeClass
    public static void start() {
        app_A = DrinkWaterApplication.create("application-A", false, true);
        app_A.addServiceBuilder(new ServiceAConfiguration());
        app_A.start();

        app_B = DrinkWaterApplication.create("application-B", false, false);
        app_B.addServiceBuilder(new ServiceBConfiguration());
        app_B.start();
    }

    @AfterClass
    public static void stop() {
        app_A.stop();
        app_B.stop();
    }

    @Test
    public void testTracing() {
        String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();
    }
}
