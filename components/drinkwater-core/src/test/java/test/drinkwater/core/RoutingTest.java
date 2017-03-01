package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.Test;
import test.drinkwater.core.model.forRouting.RoutingApplicationFromConfigFile;

import java.util.HashMap;
import java.util.Map;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 6/01/2017.
 */
public class RoutingTest extends HttpUnitTest {

    @Test
    public void shouldRouteCorrectly() throws Exception {

        try (DrinkWaterApplication app = DrinkWaterApplication.create("routing-test",
                options().use(RoutingApplicationFromConfigFile.class).autoStart())) {

            String port = (String)app.getServiceProperty("frontService", RestService.REST_PORT_KEY);

            String frontHost = String.format("http://localhost:%s/frontService/data", port);
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

            //Route with no headers => default
            result = httpGetString(frontHost).result();
            assertEquals("propertyFromdefault", result);

            //Route to B again with Options
            headers.replace("ROUTINGHEADER", "B");
            result = httpOptions(frontHost, headers).result();
            assertEquals("OK", result);

            //Route to SubRouting to Y
            headers.replace("ROUTINGHEADER", "sub");
            headers.put("SUBROUTINGHEADER", "Y");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromY", result);

            //Route to SubRouting to X
            headers.replace("ROUTINGHEADER", "sub");
            headers.replace("SUBROUTINGHEADER", "X");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromX", result);

            //Route to SubRouting to default
            headers.replace("ROUTINGHEADER", "sub");
            headers.remove("SUBROUTINGHEADER");
            result = httpGetString(frontHost, headers).result();
            assertEquals("propertyFromdefault", result);

        }
    }

}
