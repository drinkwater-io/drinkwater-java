package test.drinkwater.examples.remote;

import drinkwater.test.HttpUnitTest;

//import org.mockserver.client.server.MockServerClient;
//import org.mockserver.junit.MockServerRule;
//import org.mockserver.model.HttpResponse;
//import static org.mockserver.model.HttpRequest.request;

/**
 * Unit test for simple App.
 */
public class MultiServiceRemoteTest extends HttpUnitTest {

//    static DrinkWaterApplication app;
//
//    @ClassRule
//    public static WireMockClassRule wireMockRule = new WireMockClassRule(
//            options().dynamicPort());
//
//    @Rule
//    public WireMockClassRule instanceRule = wireMockRule;
//
//    @BeforeClass
//    public static void setup() throws Exception {
//        app = DrinkWaterApplication
//                .create("remote-dwapp",
//                        ApplicationOptionsBuilder.options()
//                .use(MultiServiceRemoteApplication.class)
//                .autoStart());
//
//    }
//
//    @AfterClass
//    public static void tearDown() throws Exception {
//        app.stop();
//    }
//
//    @Before
//    public void setExpectation(){
//        //Sets the expectation for wiremock
//        stubFor(get(urlEqualTo("/serviceD/uppercaseData?data=someData"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "text/plain")
//                        .withBody("mocked Data from D")));
//    }
//
//    @Test
//    public void ShouldCallRemoteService() throws UnirestException {
//
//        //TODO change the property setting using a constant
//        IServiceConfiguration config = (IServiceConfiguration)
//                ServiceConfiguration.empty().addInitialProperty("drinkwater.rest.port", wireMockRule.port()).asRemote();
//
//        app.patchService("serviceD", config);
//
//        IServiceD serviceD = app.getService("serviceD");
//
//        String transformed = serviceD.uppercaseData("someData");
//
//        assertEquals("mocked Data from D", transformed);
//    }
//
//    @Test
//    public void ShouldCallRemoteServiceFromDependencies() throws UnirestException {
//
//        //TODO change the property setting using a constant
//        IServiceConfiguration config = (IServiceConfiguration)
//                ServiceConfiguration.empty().addInitialProperty("drinkwater.rest.port", wireMockRule.port()).asRemote();
//
//        //not remote
//        IServiceA serviceA = app.getService("serviceA");
//
//        String transformed = serviceA.getData("someData");
//
//        assertEquals("A -> [B -> [C -> [servicCConn : someData] - D -> [SOMEDATAtoappend]]]", transformed);
//
//        //patch the service to be a remote one (calling the mockserver)
//
//        app.patchService("serviceD", config);
//
//        serviceA = app.getService("serviceA");
//
//        transformed = serviceA.getData("someData");
//
//        assertEquals("A -> [B -> [C -> [servicCConn : someData] - mocked Data from D]]", transformed);
//    }

}
