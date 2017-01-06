package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.EventAggregator;
import org.junit.BeforeClass;
import org.junit.Test;
import test.drinkwater.core.model.forTracing.ServiceAConfiguration;
import test.drinkwater.core.model.forTracing.ServiceBConfiguration;
import test.drinkwater.core.model.forTracing.ServiceCConfiguration;

import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 5/01/2017.
 */
public class TracingTest extends HttpUnitTest {

    static DrinkWaterApplication app_A;
    static DrinkWaterApplication app_B;
    static DrinkWaterApplication app_C;

    @BeforeClass
    public static void start() {
        app_C = DrinkWaterApplication.create("application-C", false, false);
        app_C.addServiceBuilder(new ServiceCConfiguration());

        app_B = DrinkWaterApplication.create("application-B", false, false);
        app_B.addServiceBuilder(new ServiceBConfiguration());

        app_A = DrinkWaterApplication.create("application-A", false, true);
        app_A.addServiceBuilder(new ServiceAConfiguration());
    }


    @Test
    public void testTracing() {

        app_A.start();
        app_B.start();
        app_C.start();

        String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();

        assertEquals("data from A - data from B - data from c /computed in D/ ", result);

        EventAggregator aggregator = app_A.getEventAggregator();

        app_A.stop();
        app_B.stop();
        app_C.stop();

        assertEquals(12, aggregator.currentSize());
    }
}
