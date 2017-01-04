package test.drinkwater.core;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mashape.unirest.http.exceptions.UnirestException;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.drinkwater.core.model.TestConfiguration;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceManagementRestTest extends HttpUnitTest {

    static DrinkWaterApplication app;
    private static String MANAGEMENT_API_ENDPOINT = "http://localhost:8889/core-test";
    private static String DEFAULT_REST_HOST_AND_PORT = "http://localhost:8889";

    @BeforeClass
    public static void start() {
        app = DrinkWaterApplication.create("core-test");
        app.addServiceBuilder(new TestConfiguration());
        app.start();
    }

    @AfterClass
    public static void stop() {
        app.stop();
    }

    @Test
    public void checkTestServiceEndpoint() throws UnirestException {
        httpGetString(DEFAULT_REST_HOST_AND_PORT + "/test/info").expectsBody("test info");

        MetricRegistry registry = app.getTracer().getMetrics();

        Map<String, Metric> metrics = registry.getMetrics();

        assertNotNull(metrics.get("TestServiceImpl.getInfo"));
        assertTrue(metrics.get("TestServiceImpl.getInfo") instanceof Timer);
    }


    @Test
    public void checkServiceNames() throws UnirestException {
        httpGet(MANAGEMENT_API_ENDPOINT + "/servicenames")
                .expectsBody("['test']")
                .expectsStatus(200);

    }


    @Test
    public void shouldStartAndStopService() throws UnirestException {
        httpPostRequestString(MANAGEMENT_API_ENDPOINT + "/stopservice?serviceName=test", "")
                .expectsBody("Service test stopped")
                .expectsStatus(200);

        httpGetString(MANAGEMENT_API_ENDPOINT + "/ServiceState?serviceName=test")
                .expectsBody("Stopped")
                .expectsStatus(200);

        httpPostRequestString(MANAGEMENT_API_ENDPOINT + "/startservice?serviceName=test", "")
                .expectsBody("Service test started")
                .expectsStatus(200);

        httpGetString(MANAGEMENT_API_ENDPOINT + "/ServiceState?serviceName=test")
                .expectsBody("Up")
                .expectsStatus(200);

    }


}
