package test.drinkwater.examples.remote;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.remote.MultiServiceRemoteConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpResponse;

import static org.mockserver.model.HttpRequest.request;

/**
 * Unit test for simple App.
 */
public class MultiServiceRemoteTest {

    static DrinkWaterApplication app;

    static {
        //FIXME manage the logging system it's shity
        System.setProperty("logback.configurationFile", "mockserver_logback.xml");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;

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

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/test")
        ).respond(
                HttpResponse.response().withBody("{ message: 'incorrect username and password combination' }")
        );

        System.out.println("test");
    }
}
