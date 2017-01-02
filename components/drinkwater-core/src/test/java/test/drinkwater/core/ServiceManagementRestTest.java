package test.drinkwater.core;

import com.mashape.unirest.http.exceptions.UnirestException;
import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        app.addServiceBuilder(new ServiceConfigurationBuilder() {
                                  @Override
                                  public List<IServiceConfiguration> build() {
                                      List<IServiceConfiguration> configs = new ArrayList<IServiceConfiguration>();
                                      configs.add(ServiceConfiguration
                                              .forService(ITestService.class)
                                              .name("test")
                                              .useBean(new TestServiceImpl())
                                              .asRest());

                                      return configs;
                                  }
                              }
        );

        app.start();
    }

    @AfterClass
    public static void stop() {
        app.stop();
    }

    @Test
    public void checkTestServiceEndpoint() throws UnirestException {
        httpGetString(DEFAULT_REST_HOST_AND_PORT + "/test/testinfo").expectsBody("test info");
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
