package test.drinkwater.core;

import com.mashape.unirest.http.exceptions.UnirestException;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.drinkwater.core.model.TestConfiguration;

import static drinkwater.ApplicationOptionsBuilder.options;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceManagementRestTest extends HttpUnitTest {

    static DrinkWaterApplication app;
    private static String MANAGEMENT_API_ENDPOINT = "http://localhost:8889/service-management";
    private static String DEFAULT_REST_HOST_AND_PORT = "http://localhost:8889";

    @BeforeClass
    public static void start() {
        app = DrinkWaterApplication.create("service-management",
                options().use(TestConfiguration.class).autoStart());
    }

    @AfterClass
    public static void stop() {
        if(app != null) {
            app.stop();
        }
    }

    @Test
    public void checkTestServiceEndpoint() throws UnirestException {
        httpGetString(DEFAULT_REST_HOST_AND_PORT + "/test/info").expectsBody("test info");

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
