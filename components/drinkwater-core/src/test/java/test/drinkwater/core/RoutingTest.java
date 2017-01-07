package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.Test;
import test.drinkwater.core.model.forRouting.RoutingServiceConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 6/01/2017.
 */
public class RoutingTest extends HttpUnitTest {

    @Test
    public void shouldRouteCorrectly() {
        DrinkWaterApplication app = DrinkWaterApplication.create("routing-test", false);
        app.addServiceBuilder(new RoutingServiceConfiguration());

        try {
            app.start();

            String frontHost = "http://localhost:8889/frontService/data";
            Map<String, String> headers = new HashMap<>();

            //Route to A
            headers.put("ROUTINGHEADER", "A");
            String result = httpGetString(frontHost, headers).result();

            assertEquals("propertyFromA", result);

            //Route to B
            headers.replace("ROUTINGHEADER", "B");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromB", result);

            //Route to C
            headers.replace("ROUTINGHEADER", "C");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromC", result);

            //Route to B again
            headers.replace("ROUTINGHEADER", "B");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromB", result);
        } finally {
            app.stop();
        }


    }


}
