package test.drinkwater.examples.remote;

import com.mashape.unirest.http.exceptions.UnirestException;
import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfiguration;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceD;
import drinkwater.examples.remote.MultiServiceRemoteConfiguration;
import drinkwater.test.HttpUnitTest;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpRequest.request;

/**
 * Unit test for simple App.
 */
public class MultiServiceRemoteTest extends HttpUnitTest {

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
        app = DrinkWaterApplication.create("remote-dwapp", false);
        MultiServiceRemoteConfiguration config = new MultiServiceRemoteConfiguration();
        app.addServiceBuilder(config);
        app.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
    }

    @Before
    public void setExpectations() {
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/serviceD/uppercaseData")
                        .withQueryStringParameter("data", "someData")
        ).respond(
                HttpResponse.response().withBody("mocked Data from D")
        );
    }


    @Test
    public void ShouldCallRemoteService() throws UnirestException {

        //TODO change the property setting using a constant
        IServiceConfiguration config = (IServiceConfiguration)
                ServiceConfiguration.empty().withProperty("drinkwater.rest.port", mockServerRule.getPort()).asRemote();

        app.patchService("serviceD", config);

        IServiceD serviceD = app.getService("serviceD");

        String transformed = serviceD.uppercaseData("someData");

        assertEquals("mocked Data from D", transformed);
    }

    @Test
    public void ShouldCallRemoteServiceFromDependencies() throws UnirestException {

        //TODO change the property setting using a constant
        IServiceConfiguration config = (IServiceConfiguration)
                ServiceConfiguration.empty().withProperty("drinkwater.rest.port", mockServerRule.getPort()).asRemote();

        //not remote
        IServiceA serviceA = app.getService("serviceA");

        String transformed = serviceA.getData("someData");

        assertEquals("A -> [B -> [C -> [servicCConn : someData] - D -> [SOMEDATAtoappend]]]", transformed);

        //patch the service to be a remote one (calling the mockserver)

        app.patchService("serviceD", config);

        serviceA = app.getService("serviceA");

        transformed = serviceA.getData("someData");

        assertEquals("A -> [B -> [C -> [servicCConn : someData] - mocked Data from D]]", transformed);
    }

//    @Test
//    public void TestTracing() throws UnirestException {
//
//        IServiceConfiguration config = (IServiceConfiguration)
//                ServiceConfiguration.empty().withProperty("drinkwater.rest.port", mockServerRule.getPort()).asRemote();
//
//        app.patchService("serviceD", config);
//
//        String result = httpGetString("http://localhost:8889/serviceA/data?data=someData").result();
//
//        assertEquals("A -> [B -> [C -> [servicCConn : someData] - mocked Data from D]]", result);
//
//        //assertEquals(2, app.getEventAggregator().currentSize());
//    }
}
